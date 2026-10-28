#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$ROOT"
if [[ -d run ]]; then
  for file in run/*.pid; do
    [[ -e "$file" ]] || continue
    pid="$(cat "$file")"
    kill "$pid" >/dev/null 2>&1 || true
    rm -f "$file"
  done
fi
docker compose stop >/dev/null 2>&1 || true
echo "兔南呱呱同城网已停止。"
