# Pyrotechnics - a minecraft mod

default:
    just --list

# Decompile minecraft
gen:
    ./gradlew genSources

# Build the mod jar
build:
    ./gradlew build
    ls -lh build/libs/*.jar

# Run Minecraft with the mod
test:
    ./gradlew runClient

# Clean build artifacts
clean:
    ./gradlew clean

# Enter nix dev shell (if not already in one)
dev:
    nix develop

# Delete the loom cache (source files)
clean-loom:
    rm -rf .gradle/loom-cache

# Git: add everything and commit
commit m:
    git add -A
    git commit -m "{{m}}"
