@echo off
echo ========================================
echo  Starting Barista Dashboard
echo ========================================
echo.

cd frontend
call mvn javafx:run -Djavafx.mainClass="com.virtualcafe.frontend.BaristaDashboard"

cd ..
pause
