module.exports = {
  entry: {
    "samples-metadata-import": "./resources/js/dev/samples-metadata-import.js"
  },
  module: {
    loaders: [
      {
        test: /.js?$/,
        loader: 'babel-loader',
        exclude: /node_modules/,
        query: {
          presets: ['es2015', 'stage-0']
        }
      },
      {test: /\.js$/, loader: "eslint-loader", exclude: /node_modules/}
    ]
  },
  externals: {
    // require("jquery") is external and available
    //  on the global var jQuery
    jquery: "jQuery",
    angular: "angular"
  },
  eslint: {
    configFile: "./.eslintrc.json"
  },
  output: {
    filename: '[name].bundle.js'
  }
};
