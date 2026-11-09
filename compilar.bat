@echo off
chcp 65001 > nul
cd src
javac -encoding UTF-8 --release 17 *.java
if %errorlevel% neq 0 (
    echo Erro na compilacao.
    exit /b 1
)
echo Compilado com sucesso. Para rodar: cd src ^&^& java Main
