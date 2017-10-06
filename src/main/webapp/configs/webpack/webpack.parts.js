const CleanWebpackPlugin = require("clean-webpack-plugin");
const ProgressBarPlugin = require("progress-bar-webpack-plugin");
const WriteFilePlugin = require("write-file-webpack-plugin");

/**
 * Compiles and live reloads page during development.
 * DO NOT LOAD INTO PRODUCTION CONFIGURATION.
 * @param host Specify a host to use. By default this is localhost.
 * @param port Specify a port number to listen for requests on
 * @param proxy Proxying some URLs can be useful when you have a separate API backend development server and you want to send API requests on the same domain.
 * @returns {{devServer: {historyApiFallback: boolean, stats: string, host, port, proxy, overlay: {errors: boolean, warnings: boolean}}}}
 */
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

/**
 * Enforce eslint standard are enforced.
 * @returns {{module: {rules: *[]}}}
 */
exports.lintJavaScript = () => ({
  module: {
    rules: [
      {
        test: /\.js$/,
        exclude: /node_modules/,
        loader: "eslint-loader"
      }
    ]
  }
});

/**
 * style-loader: Adds CSS to the DOM by injecting a <style> tag
 * css-loader: interprets @import and url() like import/require() and will resolve them.
 * @returns {{module: {rules: *[]}}}
 */
exports.loadCSS = () => ({
  module: {
    rules: [{ test: /\.css$/, loader: "style-loader!css-loader" }]
  }
});

/**
 * Forces webpack-dev-server program to write bundle files to the file system.
 * @returns {{plugins: *[]}}
 */
exports.writeFilePlugin = () => ({
  plugins: [
    new WriteFilePlugin({
      log: false,
      test: /\.(css|js)/
    })
  ]
});

/**
 * Remove directories listed in the paths
 * @param {list} paths to remove
 * @param {object} options
 *    - verbose: write logs {boolean}
 *    - dry: actually remove the files {boolean}
 *    - allowExternals: Allow the plugin to clean folders outside of the webpack
 *      root. {boolean}
 * @returns {{plugins: *[]}}
 */
exports.clean = (
  paths = [],
  options = { verbose: false, dry: false, allowExternal: true }
) => ({
  plugins: [new CleanWebpackPlugin(paths, options)]
});

/**
 * Display a progress bar for the webpack build
 * @returns {{plugins: *[]}}
 */
exports.progressBar = () => ({
  plugins: [new ProgressBarPlugin()]
});
