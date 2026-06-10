@echo off
echo ========================================
echo  Virtual Cafe Java - Launch All (Maven)
echo ========================================
echo.
echo This will open 4 windows:
echo   1. Spring Boot Backend (must stay open)
echo   2. Customer CLI
echo   3. Customer GUI
echo   4. Barista Dashboard
echo.
echo IMPORTANT: Make sure you have built the project first!
echo            Run 1-compile.bat if you haven't.
echo.
pause

echo.
echo Starting Spring Boot Backend...
start "Spring Boot Backend API" cmd /k 2-run-server.bat

echo Waiting for backend to start (Warm up)...
timeout /t 5 /nobreak > nul

echo Starting Customer CLI...
start "Customer CLI" cmd /k 3-run-customer-cli.bat

echo Starting Customer GUI...
start "Customer GUI" cmd /k 4-run-customer-gui.bat

echo Starting Barista Dashboard...
start "Barista Dashboard" cmd /k 5-run-barista-dashboard.bat

echo.
echo ========================================
echo  All applications launched!
echo ========================================
echo.
echo You can now close this window.
pause
