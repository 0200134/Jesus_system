{
  description = "J.E.S.U.S System & eGovFrame Omni-Platform Arena (Linux, macOS, Windows & Windows Server)";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
  };

  outputs = { self, nixpkgs, ... }:
    let
      supportedSystems = [ "aarch64-darwin" "x86_64-linux" "x86_64-windows" ];
      forEachSystem = f: nixpkgs.lib.genAttrs supportedSystems (system: f {
        pkgs = import nixpkgs { inherit system; };
      });

      # Reproducibility hash of the vendored Maven dependency repository
      # (fixed-output derivation → no network access during the actual build).
      # If it mismatches, Nix prints the correct hash in the build error — paste it here.
      mvnHash = "sha256-AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=";

      # Shared builder: bit-reproducible Maven build with hash-pinned dependencies.
      buildJesusServer = { pkgs, jdk, pname }:
        pkgs.maven.buildMavenPackage {
          inherit pname;
          version = "1.0.0";
          src = ./.;
          mvnJdk = jdk;
          inherit mvnHash;
          nativeBuildInputs = [ pkgs.makeWrapper ];
          installPhase = ''
            mkdir -p $out/bin $out/share
            cp target/*-exec.jar $out/share/app.jar
            makeWrapper ${jdk}/bin/java $out/bin/run-jesus \
              --add-flags "-jar $out/share/app.jar"
          '';
          # Enables `nix run .#<pname-suffix>` used by the resurrection protocol.
          meta.mainProgram = "run-jesus";
        };
    in {
      # 1. 멀티 플랫폼 로컬 개발 및 관리 셸 (devShells)
      devShells = forEachSystem ({ pkgs }: {
        default = pkgs.mkShell {
          buildInputs = with pkgs; [ jdk17 maven gradle git jq ];
          shellHook = ''
            echo "=================================================="
            echo " [✝️] J.E.S.U.S Arena: Java 17 (Legacy / Win-Server) "
            echo " Target System   : ${pkgs.stdenv.system}"
            echo " Java Version    : $(java -version 2>&1 | head -n 1)"
            echo "=================================================="
          '';
        };

        java25 = pkgs.mkShell {
          buildInputs = with pkgs; [ jdk25 maven gradle git jq ];
          shellHook = ''
            echo "=================================================="
            echo " [⚡] J.E.S.U.S Arena: Java 25 (Cutting-Edge Core) "
            echo " Target System   : ${pkgs.stdenv.system}"
            echo " Java Version    : $(java -version 2>&1 | head -n 1)"
            echo "=================================================="
          '';
        };
      });

      # 2. 멀티OS / 멀티버전 배포 아티팩트 빌드 (Packages)
      packages = forEachSystem ({ pkgs }: {
        jesus-server-17 = buildJesusServer { inherit pkgs; jdk = pkgs.jdk17; pname = "jesus-system-jdk17"; };
        jesus-server-25 = buildJesusServer { inherit pkgs; jdk = pkgs.jdk25; pname = "jesus-system-jdk25"; };
      });
    };
}
