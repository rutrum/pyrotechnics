#!/usr/bin/env bash
# Look up a Yarn class/method/field in the decompiled Minecraft 26.2 sources
# Usage: ./lookup.sh <class-name> [filter]
#   ./lookup.sh Block destroy
#   ./lookup.sh "Level.isClientSide"
#   ./lookup.sh "ContainerHelper saveAll"

set -e
REPO="$(cd "$(dirname "$0")" && pwd)"
COMMON_JAR="$REPO/.gradle/loom-cache/minecraftMaven/net/minecraft/minecraft-common-043a8b3edf/26.2/minecraft-common-043a8b3edf-26.2-sources.jar"
CLIENT_JAR="$REPO/.gradle/loom-cache/minecraftMaven/net/minecraft/minecraft-clientOnly-043a8b3edf/26.2/minecraft-clientOnly-043a8b3edf-26.2-sources.jar"

if [ ! -f "$COMMON_JAR" ]; then
    echo "Sources not found. Run: nix develop --command ./gradlew genSources"
    exit 1
fi

CLASS="$1"
FILTER="${2:-.}"

# Try to find the class in the common jar first, then client jar
for jar in "$COMMON_JAR" "$CLIENT_JAR"; do
    ENTRY=$(jar tf "$jar" 2>/dev/null | grep -i "/${CLASS}.java$" | head -1)
    if [ -n "$ENTRY" ]; then
        unzip -p "$jar" "$ENTRY" 2>/dev/null | grep -in "$FILTER" | head -30
        exit 0
    fi
done

# Try partial match
for jar in "$COMMON_JAR" "$CLIENT_JAR"; do
    ENTRY=$(jar tf "$jar" 2>/dev/null | grep -i "$CLASS" | grep "\.java$" | head -1)
    if [ -n "$ENTRY" ]; then
        echo "Found: $ENTRY"
        unzip -p "$jar" "$ENTRY" 2>/dev/null | grep -in "$FILTER" | head -30
        exit 0
    fi
done

echo "Class '$CLASS' not found in sources."