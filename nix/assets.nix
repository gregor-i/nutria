{ pkgs, fs }:
pkgs.buildNpmPackage {
  pname = "nutria-assets";
  version = "0.1.0";
  npmDepsHash = "sha256-vF6+lhVOgUxBFHbbU9C7DkZpo2yU5S01HatJ6NfNGVg=";

  src = fs.toSource {
    root = ./..;
    fileset = fs.unions [
      ../package.json
      ../package-lock.json
      ../frontend/src/main/css
      ../frontend/src/main/html
      ../frontend/src/main/img
    ];
  };

  dontNpmBuild = true;

  installPhase = ''
    mkdir -p backend/public/html
    mkdir -p backend/public/assets
    npm run build-css
    npm run build-html
    npm run build-img
    npm run build-fonts

    cp -r backend/public $out
  '';
}
