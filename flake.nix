{
  description = "Pyrotechnics - A Minecraft mod for intuitive firework crafting";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs { inherit system; };
      in
      {
        devShells.default = pkgs.mkShell {
          nativeBuildInputs = with pkgs; [
            jdk25
            gradle_9
            uv
            libglvnd
          ];

          # Gradle needs to know where to find JDK 25
          GRADLE_OPTS = "-Dorg.gradle.java.home=${pkgs.jdk25.home}";

          # LD_LIBRARY_PATH must include the GPU driver and GL dispatch libraries
          # for LWJGL/OpenGL to work (runClient, etc.)
          LD_LIBRARY_PATH = "/run/opengl-driver/lib:${pkgs.libglvnd}/lib";

          shellHook = ''
            echo "Pyrotechnics dev shell"
            echo "Java: $(java -version 2>&1 | head -1)"
            echo "Gradle: $(gradle --version 2>&1 | grep 'Gradle ')"
            echo "LD_LIBRARY_PATH: $LD_LIBRARY_PATH"
          '';
        };

        # Convenience: `nix build` produces the mod jar
        packages.default = pkgs.stdenv.mkDerivation {
          name = "pyrotechnics";
          src = self;
          buildInputs = with pkgs; [ jdk gradle ];
          buildPhase = ''
            gradle build --no-daemon
          '';
          installPhase = ''
            mkdir -p $out
            cp build/libs/*.jar $out/
          '';
        };
      }
    );
}
