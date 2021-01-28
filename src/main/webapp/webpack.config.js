const path = require("path");

module.exports = {
  entry: "./resources/js/app.js",
  output: {
    filename: "main.bundle.js",
    path: path.resolve(__dirname, "dist/js"),
  },
  module: {
    rules: [
      {
        test: /\.(js|jsx)$/i,
        use: 'babel-loader',
        exclude: /node_modules/,
      },
      {
        test: /\.less$/i,
        use: [
            "style-loader",
            "css-loader",
            "less-loader",
        ],
      },
      {
        test: /\.css$/i,
        use: ["style-loader", "css-loader", "postcss-loader"],
      },
    ]},
  resolve: {
    extensions: [ '.js', '.jsx' ],
  },
};
