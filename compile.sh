#!/bin/bash

set -e

# Répertoires
LIB="lib"
BIN="bin"
BUILD="build"
GSON="$LIB/gson-2.14.0.jar"
SERVLET="$LIB/servlet-api.jar"

# Vérification des dépendances
if [ ! -f "$GSON" ]; then
    echo "Erreur : Gson introuvable : $GSON"
    exit 1
fi

if [ ! -f "$SERVLET" ]; then
    echo "Erreur : Servlet API introuvable : $SERVLET"
    exit 1
fi

# Nettoyage
rm -rf "$BIN" "$BUILD"
mkdir -p "$BIN" "$BUILD"

# Recherche des sources
find . -path "./bin" -prune -o \
       -path "./build" -prune -o \
       -path "./.git" -prune -o \
       -name "*.java" -print > sources.txt

# Compilation
javac -cp "$SERVLET:$GSON" -d "$BIN" @sources.txt

rm sources.txt

# Préparation du JAR autonome
cp -r "$BIN"/. "$BUILD"/

(
    cd "$BUILD"
    jar xf "../$GSON"
)

# Création du JAR final
jar cf framework.jar -C "$BUILD" .

echo "Compilation terminée : framework.jar"