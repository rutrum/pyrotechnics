# Decompile minecraft
gen:
    ./gradlew genSources

# Create mod jar file
build:
    ./gradlew build
    ls build/libs/*.jar

# See version numbers on fabricmc.net
versions:
    firefox "https://fabricmc.net/develop/"

# Reset mapping to dependencies, mapping in vscode
fix:
    ./gradlew --refresh-dependencies
    ./gradlew vscode
    echo "Reload build.gradle in vscode!!!"

# Deletes the loom cache (source files)
clean-loom:
    trash .gradle/loom-cache

# Runs minecraft with mod
test:
    ./gradlew runClient

clean:
    ./gradlew clean
