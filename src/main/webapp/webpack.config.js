const path = require("path");
let entries = require("./configs/webpack/entries.js");

const PATHS = {
  build: path.join(__dirname, "resources/js/build")
};

const commonConfig = () => ({
  entry: entries,
  devtool: "source-maps",
  module: {
    rules: [
      {
        test: /\.js$/,
        loader: "babel-loader",
        exclude: /node_modules/,
        options: {
          // Enable caching for improved performance during
          // development.
          // It uses default OS directory by default. If you need
          // something more custom, pass a path to it.
          // I.e., { cacheDirectory: '<path>' }
          cacheDirectory: true
        }
      },
      { test: /\.css$/, loader: "style-loader!css-loader" }
    ]
  },
  externals: {
    // require('jquery') is external and available
    //  on the global var jQuery
    jquery: "jQuery",
    angular: "angular",
    moment: "moment"
  },
  output: {
    path: PATHS.build,
    filename: "[name].bundle.js"
  }
});

const productionConfig = () => commonConfig();

const developmentConfig = () => {
  const config = {
    devServer: {
      // Enable history API fallback so HTML5 History API based
      // routing works. Good for complex setups.
      historyApiFallback: true,
      overlay: {
        warnings: true,
        errors: true
      },
      proxy: {
        "/": "http://localhost:8080"
      },
      hot: true,

      // Display only errors to reduce the amount of output.
      stats: "errors-only",

      // Parse host and port from env to allow customization.
      //
      // If you use Docker, Vagrant or Cloud9, set
      // host: options.host || '0.0.0.0';
      //
      // 0.0.0.0 is available to all network devices
      // unlike default `localhost`.
      host: process.env.HOST, // Defaults to `localhost`
      port: process.env.PORT // Defaults to 8080
    }
  };

  return Object.assign({}, commonConfig(), config);
};

module.exports = env => {
  console.log(env);
  if (env.target === "production") {
    return productionConfig();
  }

  return developmentConfig();
};
