@echo off
echo ========================================
echo  Starting Customer CLI
echo ========================================
echo.

cd frontend
call mvn exec:java -Dexec.mainClass="com.virtualcafe.frontend.CustomerCLI"

cd ..
pause
