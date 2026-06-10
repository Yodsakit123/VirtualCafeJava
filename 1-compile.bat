@echo off
echo ========================================
echo  Compiling Virtual Cafe with Maven
echo ========================================
echo.

call mvn clean package -DskipTests

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo  Compilation & Build Successful!
    echo ========================================
) else (
    echo.
    echo ========================================
    echo  Compilation Failed! Check Maven output.
    echo ========================================
)

echo.
pause
