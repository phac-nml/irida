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

/* ======================
 PRODUCTION CONFIGURATION
====================== */
const productionConfig = merge([]);

/* =======================
 DEVELOPMENT CONFIGURATION
 ====================== */
const developmentConfig = merge([
  {
    devtool: "inline-source-map"
  },
  parts.devServer({
    host: process.env.HOST,
    port: 3000,
    proxy: {
      "/": {
        target: "http://localhost:8080",
        secure: false,
        prependPath: false
      }
    }
  }),
  // Add this back in after we format the entire project!
  parts.lintJavaScript({ include: Object.values(entries) }),
  parts.writeFilePlugin()
]);

module.exports = env => {
  console.log(env);
  if (env.target === "production") {
    return merge(commonConfig, productionConfig);
  }

  return merge(commonConfig, developmentConfig);
};
