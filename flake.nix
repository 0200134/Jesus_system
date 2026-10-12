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
        jesus-server-17 = pkgs.stdenv.mkDerivation {
          pname = "jesus-system-jdk17";
          version = "1.0.0";
          src = ./.;
          buildInputs = [ pkgs.jdk17 pkgs.maven ];
          buildPhase = "mvn clean package -DskipTests";
          installPhase = ''
            mkdir -p $out/bin $out/share
            cp target/*.jar $out/share/app.jar
            echo "java -jar $out/share/app.jar" > $out/bin/run-jesus.bat
            chmod +x $out/bin/run-jesus.bat
          '';
        };

        jesus-server-25 = pkgs.stdenv.mkDerivation {
          pname = "jesus-system-jdk25";
          version = "1.0.0";
          src = ./.;
          buildInputs = [ pkgs.jdk25 pkgs.maven ];
          buildPhase = "mvn clean package -DskipTests";
          installPhase = ''
            mkdir -p $out/bin $out/share
            cp target/*.jar $out/share/app.jar
            echo "java -jar $out/share/app.jar" > $out/bin/run-jesus.bat
            chmod +x $out/bin/run-jesus.bat
          '';
        };
      });
    };
}
