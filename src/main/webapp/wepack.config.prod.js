const path = require("path");
const OptimizeCSSAssetsPlugin = require("optimize-css-assets-webpack-plugin");
const cssnano = require("cssnano");
const CleanWebpackPlugin = require("clean-webpack-plugin");

const BUILD_PATH = path.resolve(__dirname, "resources/dist");
exports.config = {
  mode: "production",
  devtool: "source-map",
  plugins: [
    new CleanWebpackPlugin([BUILD_PATH]),
    new OptimizeCSSAssetsPlugin({
      cssProcessor: cssnano,
      cssProcessorOptions: {
        discardComments: {
          removeAll: true
        },
        // Run cssnano in safe mode to avoid
        // potentially unsafe transformations.
        safe: true,
        canPrint: false
      }
    })
  ]
};
