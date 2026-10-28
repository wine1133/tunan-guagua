[CmdletBinding()]
param(
    [switch]$SkipBuild,
    [switch]$NoBrowser
)

$ErrorActionPreference = 'Stop'
$Root = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $Root
$RunDir = Join-Path $Root 'run'
$LogDir = Join-Path $Root 'logs'
New-Item -ItemType Directory -Force -Path $RunDir, $LogDir | Out-Null

function Wait-Tcp {
    param([string]$HostName, [int]$Port, [int]$TimeoutSeconds = 120)
    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    while ((Get-Date) -lt $deadline) {
        $client = [System.Net.Sockets.TcpClient]::new()
        try {
            $task = $client.ConnectAsync($HostName, $Port)
            if ($task.Wait(1000) -and $client.Connected) { return $true }
        } catch { }
        finally { $client.Dispose() }
        Start-Sleep -Seconds 2
    }
    return $false
}

function Wait-Health {
    param([string]$Url, [int]$TimeoutSeconds = 150)
    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    while ((Get-Date) -lt $deadline) {
        try {
            $response = Invoke-WebRequest -UseBasicParsing -Uri $Url -TimeoutSec 2
            if ($response.StatusCode -eq 200) { return $true }
        } catch { }
        Start-Sleep -Seconds 2
    }
    return $false
}

function Start-JavaService {
    param([string]$Name, [string]$Jar, [int]$Port)
    if (Wait-Tcp -HostName 'localhost' -Port $Port -TimeoutSeconds 1) {
        throw "端口 $Port 已被占用，无法启动 $Name。请先运行 stop.bat 或关闭占用端口的进程。"
    }
    $outLog = Join-Path $LogDir "$Name.out.log"
    $errLog = Join-Path $LogDir "$Name.err.log"
    $process = Start-Process -FilePath $JavaExe -ArgumentList @('-jar', $Jar) -WorkingDirectory $Root -WindowStyle Hidden -RedirectStandardOutput $outLog -RedirectStandardError $errLog -PassThru
    Set-Content -Path (Join-Path $RunDir "$Name.pid") -Value $process.Id -Encoding ascii
    Write-Host "  -> $Name PID $($process.Id), 端口 $Port"
}

Write-Host ''
Write-Host '兔南呱呱同城网 · 一键启动' -ForegroundColor Cyan
Write-Host '====================================' -ForegroundColor DarkCyan

if (-not $env:JAVA_HOME) { throw '未找到 JAVA_HOME，请先安装 JDK 17 并配置 JAVA_HOME。' }
$JavaExe = Join-Path $env:JAVA_HOME 'bin\java.exe'
if (-not (Test-Path $JavaExe)) { throw "JAVA_HOME 下未找到 java.exe：$JavaExe" }

$dockerReady = $false
try {
    docker info *> $null
    $dockerReady = $LASTEXITCODE -eq 0
} catch { $dockerReady = $false }

if (-not $dockerReady) {
    $desktopCandidates = @(
        (Join-Path $env:ProgramFiles 'Docker\Docker\Docker Desktop.exe'),
        (Join-Path $env:LOCALAPPDATA 'Docker\Docker Desktop.exe')
    )
    $desktop = $desktopCandidates | Where-Object { Test-Path $_ } | Select-Object -First 1
    if (-not $desktop) { throw 'Docker 未运行，且未找到 Docker Desktop。请先安装并启动 Docker Desktop。' }
    Write-Host 'Docker Desktop 未运行，正在启动...' -ForegroundColor Yellow
    Start-Process -FilePath $desktop -WindowStyle Hidden
    $deadline = (Get-Date).AddSeconds(150)
    while ((Get-Date) -lt $deadline) {
        try {
            docker info *> $null
            if ($LASTEXITCODE -eq 0) { $dockerReady = $true; break }
        } catch { }
        Start-Sleep -Seconds 3
    }
}
if (-not $dockerReady) { throw 'Docker Desktop 启动超时，请手动打开后重试。' }

Write-Host '[1/5] 启动 MySQL、Redis、Nacos...' -ForegroundColor Green
docker compose up -d
if ($LASTEXITCODE -ne 0) { throw 'Docker Compose 启动失败。' }

$deadline = (Get-Date).AddSeconds(180)
$mysqlHealth = ''
while ((Get-Date) -lt $deadline) {
    $mysqlHealth = docker inspect --format '{{.State.Health.Status}}' tunan-guagua-mysql 2>$null
    if ($mysqlHealth -eq 'healthy') { break }
    Start-Sleep -Seconds 3
}
if ($mysqlHealth -ne 'healthy') { throw 'MySQL 健康检查超时，请查看 docker compose logs mysql。' }

Write-Host '[2/5] 等待 Nacos 注册中心就绪...' -ForegroundColor Green
if (-not (Wait-Health -Url 'http://localhost:8848/nacos/v1/console/health/readiness' -TimeoutSeconds 180)) {
    throw 'Nacos 启动超时，请查看 docker compose logs nacos。'
}

if (-not $SkipBuild) {
    Write-Host '[3/5] 构建后端服务...' -ForegroundColor Green
    Push-Location (Join-Path $Root 'backend')
    try {
        & mvn -q -DskipTests package
        if ($LASTEXITCODE -ne 0) { throw '后端 Maven 构建失败。' }
    } finally { Pop-Location }
} else {
    Write-Host '[3/5] 跳过后端构建' -ForegroundColor DarkGray
}

$backend = Join-Path $Root 'backend'
Write-Host '[4/5] 启动微服务...' -ForegroundColor Green
Start-JavaService -Name 'user-service' -Jar (Join-Path $backend 'guagua-user-service\target\guagua-user-service-1.0.0.jar') -Port 8081
Start-JavaService -Name 'content-service' -Jar (Join-Path $backend 'guagua-content-service\target\guagua-content-service-1.0.0.jar') -Port 8082
Start-JavaService -Name 'post-service' -Jar (Join-Path $backend 'guagua-post-service\target\guagua-post-service-1.0.0.jar') -Port 8083
Start-JavaService -Name 'gateway' -Jar (Join-Path $backend 'guagua-gateway\target\guagua-gateway-1.0.0.jar') -Port 8080

$services = @(
    @{ Name = 'user-service'; Url = 'http://localhost:8081/actuator/health' },
    @{ Name = 'content-service'; Url = 'http://localhost:8082/actuator/health' },
    @{ Name = 'post-service'; Url = 'http://localhost:8083/actuator/health' },
    @{ Name = 'gateway'; Url = 'http://localhost:8080/actuator/health' }
)
foreach ($service in $services) {
    Write-Host "  等待 $($service.Name) 健康检查..."
    if (-not (Wait-Health -Url $service.Url -TimeoutSeconds 180)) {
        throw "$($service.Name) 启动失败，请查看 logs/$($service.Name).err.log 和 logs/$($service.Name).out.log。"
    }
}

Write-Host '[5/5] 检查 Vue 3 前端依赖...' -ForegroundColor Green
$frontend = Join-Path $Root 'frontend'
if (-not (Test-Path (Join-Path $frontend 'node_modules'))) {
    Push-Location $frontend
    try {
        npm install --cache .npm-cache --registry=https://registry.npmmirror.com
        if ($LASTEXITCODE -ne 0) { throw '前端依赖安装失败。' }
    } finally { Pop-Location }
}

if (Wait-Tcp -HostName 'localhost' -Port 5173 -TimeoutSeconds 1) {
    throw '端口 5173 已被占用，请先运行 stop.bat。'
}
$nodeExe = (Get-Command node).Source
$viteOut = Join-Path $LogDir 'frontend.out.log'
$viteErr = Join-Path $LogDir 'frontend.err.log'
$viteProcess = Start-Process -FilePath $nodeExe -ArgumentList @('node_modules/vite/bin/vite.js', '--host', '0.0.0.0', '--port', '5173') -WorkingDirectory $frontend -WindowStyle Hidden -RedirectStandardOutput $viteOut -RedirectStandardError $viteErr -PassThru
Set-Content -Path (Join-Path $RunDir 'frontend.pid') -Value $viteProcess.Id -Encoding ascii
if (-not (Wait-Tcp -HostName 'localhost' -Port 5173 -TimeoutSeconds 60)) {
    throw '前端启动失败，请查看 logs/frontend.err.log。'
}

Write-Host ''
Write-Host '启动完成！' -ForegroundColor Cyan
Write-Host '用户端：http://localhost:5173' -ForegroundColor White
Write-Host '管理端：http://localhost:5173/admin' -ForegroundColor White
Write-Host '网关：  http://localhost:8080' -ForegroundColor DarkGray
Write-Host 'Nacos： http://localhost:8848/nacos' -ForegroundColor DarkGray
Write-Host '普通用户：xiaonan / 123456' -ForegroundColor Yellow
Write-Host '管理员：  admin / 123456' -ForegroundColor Yellow
Write-Host '停止服务：双击 stop.bat 或运行 .\stop.ps1' -ForegroundColor DarkGray


