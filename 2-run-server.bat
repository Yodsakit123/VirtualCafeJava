@echo off
echo ========================================
echo  Starting Barista Server
echo ========================================
echo.

REM Check if compiled
if not exist "bin\BaristaServer.class" (
    echo ERROR: BaristaServer.class not found!
    echo Please run 1-compile.bat first
    pause
    exit /b 1
)

echo Server will run on port 5050
echo Keep this window open while using the system
echo Press Ctrl+C to stop the server
echo.
echo ========================================
echo.

java -cp "bin;lib\*" BaristaServer

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Server stopped with error!
)

pause
