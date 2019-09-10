const path = require("path");
const webpack = require("webpack");
const OptimizeCSSAssetsPlugin = require("optimize-css-assets-webpack-plugin");
const cssnano = require("cssnano");
const CleanWebpackPlugin = require("clean-webpack-plugin");

const BUILD_PATH = path.resolve(__dirname, "dist");
exports.config = {
  mode: "production",
  devtool: "source-map",
  optimization: {
    minimize: false,
  },
  plugins: [
    new webpack.DefinePlugin({
      "process.env.NODE_ENV": JSON.stringify("production")
    }),
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
