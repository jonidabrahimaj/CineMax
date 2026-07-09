#!/usr/bin/env bash
# ============================================================================
#  CineMax - Script di compilazione (Linux / macOS)
#  Compila i sorgenti e genera l'eseguibile CineMax.jar
# ============================================================================
set -e

echo "[1/3] Pulizia cartella bin..."
rm -rf bin
mkdir -p bin

echo "[2/3] Compilazione sorgenti Java..."
find src -name "*.java" > sources.txt
javac -encoding UTF-8 -d bin @sources.txt
rm -f sources.txt

echo "[3/3] Creazione CineMax.jar..."
jar cfe CineMax.jar cinemax.CineMax -C bin .

echo ""
echo "Fatto! Esegui l'applicazione con:"
echo "   java -jar CineMax.jar"
echo "(assicurati che la cartella 'data' sia nella stessa posizione del jar)"
