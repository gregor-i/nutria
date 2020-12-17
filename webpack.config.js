module.exports = (env, options) => {
  const devMode = options.mode !== 'production';

  return {
    entry: {
      app: [
        devMode ? './frontend/target/scala-2.13/frontend-fastopt.js' :  './frontend/target/scala-2.13/frontend-fullopt.js'
      ]
    },
    output: {
      filename: '../backend/public/assets/nutria.js',
      publicPath: '/'
    },
    devtool: devMode ? 'source-map' : undefined
  }
}
