@echo off
echo ========================================
echo  Starting Barista Dashboard
echo ========================================
echo.
echo Make sure the server is running first!
echo (Run 2-run-server.bat in another window)
echo.
echo ========================================
echo.

REM Find JavaFX folder in lib directory
set JAVAFX_PATH=
for /d %%i in (lib\javafx-sdk*) do set JAVAFX_PATH=%%i\lib

if not defined JAVAFX_PATH (
    echo ERROR: JavaFX SDK not found in lib folder!
    echo Please ensure JavaFX SDK is extracted in the lib directory.
    pause
    exit /b 1
)

echo Using JavaFX from: %JAVAFX_PATH%
echo.

java -cp "bin;lib\*" --module-path "%JAVAFX_PATH%" --add-modules javafx.controls BaristaDashboard

pause
