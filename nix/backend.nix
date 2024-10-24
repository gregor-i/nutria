{ mkSbtDerivation, fs, sbt, allAssets }:
mkSbtDerivation {
  pname = "nutria-backend";
  version = "0.1.0";
  depsSha256 = "sha256-G4K++AEcKNCNUNEbMmYIY8MDm+oj9VGX+y6r2/AWFkI=";

  src = fs.toSource {
    root = ./..;
    fileset = fs.unions [
      ../build.sbt
      ../project/build.properties
      ../project/plugins.sbt
      ../backend/app
      ../backend/conf
      ../core/src/main
      ../macros/src/main
      ../shader-builder/src/main
    ];
  };

  depsWarmupCommand = "sbt backend/stage";

  assets = allAssets;

  buildInputs = [ sbt ];
  buildPhase = ''
    mkdir -p backend/public
    cp -r $assets/* backend/public/.
    sbt backend/stage
  '';

  installPhase = ''
    cp -r backend/target/universal $out
  '';
}
