[CmdletBinding()]
param([switch]$KeepInfrastructure)

$Root = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $Root
$RunDir = Join-Path $Root 'run'

if (Test-Path $RunDir) {
    Get-ChildItem -Path $RunDir -Filter '*.pid' -File | ForEach-Object {
        $processId = (Get-Content -Raw $_.FullName).Trim()
        if ($processId) {
            try {
                Stop-Process -Id ([int]$processId) -Force -ErrorAction Stop
                Write-Host "已停止进程 $processId"
            } catch { Write-Host "进程 $processId 已不存在" -ForegroundColor DarkGray }
        }
        Remove-Item -LiteralPath $_.FullName -Force
    }
}

if (-not $KeepInfrastructure) {
    try {
        docker compose stop
        Write-Host 'MySQL、Redis、Nacos 已停止，数据卷仍然保留。' -ForegroundColor DarkGray
    } catch { Write-Host 'Docker 未运行，跳过容器停止。' -ForegroundColor DarkGray }
}
Write-Host '兔南呱呱同城网已停止。' -ForegroundColor Cyan

