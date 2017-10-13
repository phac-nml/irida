const path = require("path");
const merge = require("webpack-merge");
const parts = require("./configs/webpack/webpack.parts");
const entries = require("./configs/webpack/entries.js");

const PATHS = {
  build: path.resolve(__dirname, "resources/js/build")
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
      filename: "[name].bundle.js"
    }
  },
  parts.loadJavaScript(),
  parts.loadCSS({ exclude: /node_modules/ })
]);

/* ======================
 PRODUCTION CONFIGURATION
====================== */
const productionConfig = merge([
  parts.progressBar(),
  parts.clean([PATHS.build])
]);

/* =======================
 DEVELOPMENT CONFIGURATION
 ====================== */
const developmentConfig = merge([
  {
    devtool: "inline-source-map"
  },
  parts.devServer({
    host: process.env.HOST,
    port: 9090,
    proxy: {
      "/": {
        target: "http://localhost:8080",
        secure: false,
        prependPath: false
      }
    },
    publicPath: "http://localhost:9090/",
    historyApiFallback: true
  }),
  // Add this back in after we format the entire project!
  // parts.lintJavaScript(),
  parts.writeFilePlugin()
]);

module.exports = env => {
  if (env.target === "production") {
    return merge(commonConfig, productionConfig);
  }

  return merge(commonConfig, developmentConfig);
};
