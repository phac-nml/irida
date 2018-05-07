const webpack = require("webpack");
const CleanWebpackPlugin = require("clean-webpack-plugin");
const ProgressBarPlugin = require("progress-bar-webpack-plugin");
const ExtractTextPlugin = require("extract-text-webpack-plugin");

/**
 * Compile ES6+ code to current browser compatible JavaScript
 * @returns {{module: {rules: *[]}}}
 */
exports.loadJavaScript = () => ({
  module: {
    rules: [
      {
        test: /\.js$/,
        loader: "babel-loader",
        exclude: /node_modules/,
        options: {
          // Enable caching for improved performance during
          // development.
          // It uses default OS directory by default. If you need
          // something more custom, pass a path to it.
          // I.e., { cacheDirectory: '<path>' }
          cacheDirectory: true
        }
      }
    ]
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
 * Compress JS.  Make sure that to use angular injections.
 * @returns {{plugins: *[]}}
 */
exports.compressJavaScript = () => {
  if (process.env.MIN_JS !== "false") {
    return {
      plugins: [
        new webpack.optimize.UglifyJsPlugin()
      ]
    };
  }
};

const extractSass = new ExtractTextPlugin({
  filename: "css/[name].bundle.css"
  // disable: process.env.NODE_ENV === "development"
});

/**
 * style-loader: Adds CSS to the DOM by injecting a <style> tag
 * css-loader: interprets @import and url() like import/require() and will resolve them.
 * @returns {{module: {rules: *[]}}}
 */
exports.loadCSS = () => ({
  module: {
    rules: [
      {
        test: /\.(s?)css$/,
        use: extractSass.extract({
          use: [
            {
              loader: "css-loader"
            },
            {
              loader: "sass-loader"
            }
          ],
          // use style-loader in development
          fallback: "style-loader"
        })
      }
    ]
  },
  plugins: [extractSass]
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
  options = {
    verbose: false,
    dry: false,
    allowExternal: true
  }
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
