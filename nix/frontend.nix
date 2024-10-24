{ pkgs, mkSbtDerivation, fs, sbt, assets }:
let
  compiledFrontend = mkSbtDerivation {
    pname = "nutria-compiled-frontend";
    version = "0.1.0";
    depsSha256 = "sha256-6QTmlrf1FH8leDkFbzohR9TKf9O8/FQztl1b6L1BSd4=";

    src = fs.toSource {
      root = ./..;
      fileset = fs.unions [
        ../build.sbt
        ../project/build.properties
        ../project/plugins.sbt
        ../frontend/src/main/scala
        ../frontend/src/main/html
        ../core/src/main
        ../macros/src/main
        ../shader-builder/src/main
      ];
    };

    depsWarmupCommand = "sbt frontend/fastOptJS";

    assets = assets;

    buildInputs = [ sbt ];
    buildPhase = ''
      sbt frontend/fullOptJS
    '';

    installPhase = ''
      mkdir $out
      cp frontend/target/scala-2.13/frontend-opt.js $out/nutria.js
    '';
  };
in pkgs.buildNpmPackage {
  pname = "nutria-bundled-frontend";
  version = "0.1.0";
  npmDepsHash = "sha256-vF6+lhVOgUxBFHbbU9C7DkZpo2yU5S01HatJ6NfNGVg=";

  src = fs.toSource {
    root = ./..;
    fileset = fs.unions [ ../package.json ../package-lock.json ];
  };

  dontNpmBuild = true;

  buildInputs = [ compiledFrontend ];

  installPhase = ''
    mkdir $out
    export NODE_PATH=./node_modules
    ./node_modules/.bin/esbuild ${compiledFrontend}/nutria.js --outfile=$out/nutria.js --bundle --minify
  '';
}
