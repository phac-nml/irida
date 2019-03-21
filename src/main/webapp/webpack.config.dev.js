const webpack = require("webpack");

exports.config = {
  devtool: "eval-source-map",
  devServer: {
    proxy: {
      context: () => true,
      target: "localhost:8080"
    },
    overlay: {
      warnings: false,
      errors: true
    },
    hot: true,
    writeToDisk: true
  },
  plugins: [new webpack.HotModuleReplacementPlugin()]
};
