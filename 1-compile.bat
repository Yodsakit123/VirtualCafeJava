@echo off
echo ========================================
echo  Compiling Virtual Cafe Java Project
echo ========================================
echo.

REM Ensure we're in the right directory
if not exist "src" (
    echo ERROR: src folder not found!
    echo Make sure you're running this from VirtualCafeJava root folder
    pause
    exit /b 1
)

REM Create bin directory if it doesn't exist
if not exist "bin" (
    echo Creating bin directory...
    mkdir bin
)

REM Find JavaFX folder
set JAVAFX_PATH=
for /d %%i in (lib\javafx-sdk*) do set JAVAFX_PATH=%%i\lib

if not defined JAVAFX_PATH (
    echo ERROR: JavaFX SDK not found in lib folder!
    echo Please ensure JavaFX SDK is extracted in the lib directory.
    pause
    exit /b 1
)

echo Found JavaFX at: %JAVAFX_PATH%
echo.

REM Compile all Java files WITH JavaFX in classpath
echo Compiling source files...
echo.

javac -cp "lib\*;%JAVAFX_PATH%\*" -d bin src\*.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo  Compilation Successful!
    echo ========================================
    echo.
    echo All class files created successfully!
    echo You can now run the applications.
) else (
    echo.
    echo ========================================
    echo  Compilation Failed!
    echo ========================================
    echo.
    echo Please check the error messages above.
)

echo.
pause
