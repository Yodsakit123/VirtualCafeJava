@echo off
echo ========================================
echo  Starting Customer GUI
echo ========================================
echo.

cd frontend
call mvn javafx:run -Djavafx.mainClass="com.virtualcafe.frontend.CustomerGUI"

cd ..
pause
