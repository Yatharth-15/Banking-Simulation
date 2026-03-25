@echo off
echo [PBL] Cleaning old files...
if exist bin rmdir /s /q bin
mkdir bin

echo [PBL] Compiling Banking System...
javac -d bin src/model/*.java src/logging/*.java src/service/*.java src/DB/*.java src/gui/*.java src/main/*.java

if %errorlevel% neq 0 (
    echo [Error] Compilation failed. Check your code.
    pause
    exit /b
)

echo [PBL] Launching Application...
java -cp bin main.BankingApp
pause