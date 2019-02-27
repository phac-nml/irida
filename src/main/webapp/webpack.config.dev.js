const webpack = require("webpack");

exports.config = {
  mode: "none",
  devtool: "eval-source-map",
  devServer: {
    proxy: {
      context: () => true,
      target: "localhost:8080"
    },
    overlay: {
      warnings: true,
      errors: true
    },
    hot: true,
    writeToDisk: true
  },
  plugins: [new webpack.HotModuleReplacementPlugin()]
};
