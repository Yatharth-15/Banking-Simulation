@echo off
set "LIB_PATH=lib\mysql-connector-j-9.6.0.jar"
set "BIN_PATH=bin"

echo [PBL] Cleaning old files...
if exist %BIN_PATH% rd /s /q %BIN_PATH%
mkdir %BIN_PATH%

echo [PBL] Compiling Banking System...

dir /s /b src\*.java > sources.txt
javac -d %BIN_PATH% -cp "%LIB_PATH%" @sources.txt
if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed.
    del sources.txt
    pause
    exit /b
)
del sources.txt

echo [PBL] Launching BankingApp...
:: Since your class is in 'package main', we run 'main.BankingApp'
java -cp "%BIN_PATH%;%LIB_PATH%" main.BankingApp
pause