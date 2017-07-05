const path = require("path");
let entries = require("./configs/webpack/entries.js");

module.exports = {
  entry: entries,
  devtool: "source-maps",
  module: {
    loaders: [
      {
        test: /.js?$/,
        loader: "babel-loader",
        exclude: /node_modules/,
        query: {
          presets: ["es2015", "stage-0"]
        }
      },
      { test: /\.css$/, loader: "style-loader!css-loader" },
      { test: /\.js$/, loader: "eslint-loader", exclude: /node_modules/ }
    ]
  },
  externals: {
    // require('jquery') is external and available
    //  on the global var jQuery
    jquery: "jQuery",
    angular: "angular",
    lodash: "_"
  },
  resolve: {
    alias: {
      DataTables: path.resolve(__dirname, "resources/js/vendor/datatables/"),
      plugins: path.resolve(__dirname, "resources/js/vendor/plugins/"),
      css: path.resolve(__dirname, "resources/css/")
    }
  },
  eslint: {
    configFile: "./.eslintrc.js"
  },
  output: {
    filename: "[name].bundle.js"
  }
};
