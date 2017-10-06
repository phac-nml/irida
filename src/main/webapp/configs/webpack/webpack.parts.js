const CleanWebpackPlugin = require("clean-webpack-plugin");
const ProgressBarPlugin = require("progress-bar-webpack-plugin");
const WriteFilePlugin = require("write-file-webpack-plugin");

exports.devServer = ({ host, port, proxy } = {}) => ({
  devServer: {
    historyApiFallback: true,
    stats: "errors-only",
    host, // Defaults to `localhost`
    port, // Defaults to 8080,
    proxy,
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

exports.loadCSS = () => ({
  module: {
    rules: [{ test: /\.css$/, loader: "style-loader!css-loader" }]
  }
});

exports.writeFilePlugin = () => ({
  plugins: [
    new WriteFilePlugin({
      log: false,
      test: /\.(css|js)/
    })
  ]
});

/*
 * Remove directories listed in the path arg.
 */
exports.clean = (path, options = { verbose: false }) => ({
  plugins: [new CleanWebpackPlugin([path], options)]
});

exports.progressBar = () => ({
  plugins: [new ProgressBarPlugin()]
});
