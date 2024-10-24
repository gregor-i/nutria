{
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-23.11";
    utils.url = "github:numtide/flake-utils";
    sbtDerivation.url = "github:zaninime/sbt-derivation";
    sbtDerivation.inputs.nixpkgs.follows = "nixpkgs";
  };

  outputs = { self, nixpkgs, utils, sbtDerivation }:
    utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs { inherit system; };
        fs = pkgs.lib.fileset;
        mkSbtDerivation = sbtDerivation.mkSbtDerivation.${system};
        sbt = pkgs.sbt;

        assets = pkgs.callPackage nix/assets.nix { inherit fs pkgs; };
        frontend = pkgs.callPackage nix/frontend.nix {
          inherit pkgs mkSbtDerivation fs sbt assets;
        };

        allAssets = pkgs.symlinkJoin {
          name = "assets";
          paths = [ assets frontend ];
          postBuild = "mv $out/nutria.js $out/assets/.";
        };

        backend = pkgs.callPackage nix/backend.nix {
          inherit mkSbtDerivation fs sbt allAssets;
        };

        dockerImage = pkgs.dockerTools.buildLayeredImage {
          name = "gregor23/nutria";
          tag = self.rev or "dirty";
          config.Cmd = [
            "${pkgs.temurin-jre-bin-8}/bin/java"
            "-cp"
            "${backend}/stage/lib/*:${backend}/stage/conf/"
            "play.core.server.ProdServerStart"
          ];
        };

      in {
        packages = { inherit assets frontend allAssets backend dockerImage; };

        devShells.default = pkgs.mkShell { buildInputs = [ sbt ]; };

        formatter = pkgs.nixfmt;
      });
}
