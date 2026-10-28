@echo off
cd /d "%~dp0"
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0start.ps1"
if errorlevel 1 (
  echo.
  echo Startup failed. Please check the message above.
  pause
  exit /b 1
)
start "" "http://localhost:5173"
echo.
echo Tunan Guagua is running. Browser opened.
timeout /t 3 >nul
