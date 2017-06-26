let entries = require("./entries.js");

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
    moment: "moment",
    jquery: "jQuery",
    angular: "angular"
  },
  output: {
    filename: "[name].bundle.js"
  }
};
