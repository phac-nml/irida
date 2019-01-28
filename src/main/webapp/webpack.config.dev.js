exports.config = {
  devtool: "eval-source-map",
  devServer: {
    proxy: {
      "*": { target: "localhost:8080" }
    },
    overlay: true,
    writeToDisk: true
  }
};
