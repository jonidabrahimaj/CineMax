@echo off
REM ==========================================================================
REM  CineMax - Script di compilazione (Windows)
REM  Compila i sorgenti e genera l'eseguibile CineMax.jar
REM ==========================================================================

echo [1/3] Pulizia cartella bin...
if exist bin rmdir /s /q bin
mkdir bin

echo [2/3] Compilazione sorgenti Java...
dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -d bin @sources.txt
del sources.txt

echo [3/3] Creazione CineMax.jar...
jar cfe CineMax.jar cinemax.CineMax -C bin .

echo.
echo Fatto! Esegui l'applicazione con:
echo    java -jar CineMax.jar
echo (assicurati che la cartella 'data' sia nella stessa posizione del jar)
