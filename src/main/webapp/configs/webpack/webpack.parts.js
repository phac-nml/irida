exports.devServer = ({ host, port, proxy } = {}) => ({
  devServer: {
    historyApiFallback: true,
    stats: "errors-only",
    host, // Defaults to `localhost`
    port, // Defaults to 8080
    overlay: {
      errors: true,
      warnings: true
    }
  }
});

exports.lintJavaScript = ({ include, exclude, options }) => ({
  module: {
    rules: [
      {
        test: /\.js$/,
        include,
        exclude,
        enforce: "pre",

        loader: "eslint-loader",
        options
      }
    ]
  }
});

exports.loadCSS = ({ include, exclude, options }) => ({
  module: {
    rules: [{ test: /\.css$/, loader: "style-loader!css-loader" }]
  }
});
