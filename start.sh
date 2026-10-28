#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$ROOT"
mkdir -p run logs

if ! docker info >/dev/null 2>&1; then
  echo "Docker 未运行，请先启动 Docker Desktop。" >&2
  exit 1
fi

echo "[1/5] 启动 MySQL、Redis、Nacos..."
docker compose up -d

for _ in $(seq 1 60); do
  health="$(docker inspect --format '{{.State.Health.Status}}' tunan-guagua-mysql 2>/dev/null || true)"
  [[ "$health" == "healthy" ]] && break
  sleep 3
done
[[ "${health:-}" == "healthy" ]] || { echo "MySQL 健康检查超时。" >&2; exit 1; }

for _ in $(seq 1 60); do
  if curl -fsS http://localhost:8848/nacos/v1/console/health/readiness >/dev/null 2>&1; then break; fi
  sleep 3
done

echo "[2/5] 构建后端..."
(cd backend && mvn -q -DskipTests package)

if [[ -n "${JAVA_HOME:-}" ]]; then JAVA_BIN="$JAVA_HOME/bin/java"; else JAVA_BIN="java"; fi
BACKEND="$ROOT/backend"

start_service() {
  local name="$1" jar="$2"
  nohup "$JAVA_BIN" -jar "$jar" >"$ROOT/logs/$name.out.log" 2>"$ROOT/logs/$name.err.log" &
  echo $! >"$ROOT/run/$name.pid"
  echo "  -> $name PID $!"
}

echo "[3/5] 启动微服务..."
start_service user-service "$BACKEND/guagua-user-service/target/guagua-user-service-1.0.0.jar"
start_service content-service "$BACKEND/guagua-content-service/target/guagua-content-service-1.0.0.jar"
start_service post-service "$BACKEND/guagua-post-service/target/guagua-post-service-1.0.0.jar"
start_service gateway "$BACKEND/guagua-gateway/target/guagua-gateway-1.0.0.jar"

wait_url() {
  local name="$1" url="$2"
  echo "  等待 $name..."
  for _ in $(seq 1 90); do
    if curl -fsS "$url" >/dev/null 2>&1; then return 0; fi
    sleep 2
  done
  echo "$name 启动失败，请查看 logs/$name.err.log" >&2
  exit 1
}

wait_url user-service http://localhost:8081/actuator/health
wait_url content-service http://localhost:8082/actuator/health
wait_url post-service http://localhost:8083/actuator/health
wait_url gateway http://localhost:8080/actuator/health

echo "[4/5] 启动前端..."
cd "$ROOT/frontend"
[[ -d node_modules ]] || npm install
nohup node node_modules/vite/bin/vite.js --host 0.0.0.0 --port 5173 >"$ROOT/logs/frontend.out.log" 2>"$ROOT/logs/frontend.err.log" &
echo $! >"$ROOT/run/frontend.pid"
for _ in $(seq 1 30); do
  if curl -fsS http://localhost:5173 >/dev/null 2>&1; then break; fi
  sleep 2
done

echo "[5/5] 启动完成"
echo "用户端：http://localhost:5173"
echo "管理端：http://localhost:5173/admin"
echo "管理员：admin / 123456"
if command -v open >/dev/null 2>&1; then open http://localhost:5173; else xdg-open http://localhost:5173 >/dev/null 2>&1 || true; fi
