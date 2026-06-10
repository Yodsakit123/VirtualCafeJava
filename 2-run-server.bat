@echo off
echo ========================================
echo  Starting Spring Boot Backend API
echo ========================================
echo.

cd backend
call mvn spring-boot:run

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Backend Server stopped with error!
)

cd ..
pause
