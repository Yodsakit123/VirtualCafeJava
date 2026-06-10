@echo off
echo ========================================
echo  Starting Cafe POS
echo ========================================
echo.

cd frontend
call mvn javafx:run -Djavafx.mainClass="com.virtualcafe.frontend.CafePOS"

cd ..
pause
