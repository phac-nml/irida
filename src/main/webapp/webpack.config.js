const path = require("path");
const webpack = require("webpack");
const merge = require("webpack-merge");
const parts = require("./configs/webpack/webpack.parts");
const entries = require("./configs/webpack/entries.js");

const PATHS = {
  build: path.resolve(__dirname, "resources/dist")
};

/*
 This is used by both the production and the development configurations.
 */
const commonConfig = merge([
  {
    entry: entries,
    stats: {
      children: false,
      cached: false
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
      filename: "js/[name].bundle.js"
    },
    resolve: {
      extensions: [".js", ".jsx"]
    },
    module: {
      rules: [
        {
          test: /\.woff2?(\?v=[0-9]\.[0-9]\.[0-9])?$/,
          use: {
            loader: "url-loader"
          }
        },
        {
          test: /\.(ttf|eot|svg)(\?[\s\S]+)?$/,
          use: "file-loader"
        }
      ]
    }
  },
  parts.loadJavaScript(),
  parts.loadCSS()
]);

/* ======================
 PRODUCTION CONFIGURATION
====================== */
const productionConfig = merge([
  {
    plugins: [
      new webpack.DefinePlugin({
        "process.env.NODE_ENV": JSON.stringify("production")
      })
    ]
  },
  parts.progressBar(),
  parts.compressJavaScript(),
  parts.clean([PATHS.build])
]);

/* =======================
 DEVELOPMENT CONFIGURATION
 ====================== */
const developmentConfig = merge([
  {
    devtool: "inline-source-map"
  }
]);

module.exports = env => {
  if (env.target === "production") {
    return merge(commonConfig, productionConfig);
  }

  return merge(commonConfig, developmentConfig);
};
