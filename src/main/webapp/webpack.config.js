const path = require("path");
const merge = require("webpack-merge");
const parts = require("./configs/webpack/webpack.parts");
const entries = require("./configs/webpack/entries.js");

const PATHS = {
  build: path.join(__dirname, "resources/js/build")
};

const commonConfig = merge([
  {
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
        }
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
  },
  parts.loadCSS({ exclude: /node_modules/ })
]);

const productionConfig = merge([]);

const developmentConfig = merge([
  parts.devServer({
    host: process.env.HOST,
    port: 3000,
    proxy: {
      "/": "http://localhost:8080"
    }
  })
  // Add this back in after we format the entire project!
  // parts.lintJavaScript({ include: PATHS.app })
]);

module.exports = env => {
  console.log(env);
  if (env.target === "production") {
    return merge(commonConfig, productionConfig);
  }

  return merge(commonConfig, developmentConfig);
};
