const path = require("path");
const merge = require("webpack-merge");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");

const entries = require("./entries.js");

const BUILD_PATH = path.resolve(__dirname, "resources/dist");

const config = {
  externals: {
    jquery: "jQuery",
    angular: "angular",
    moment: "moment"
  },
  stats: {
    children: false,
    cached: false
  },
  resolve: {
    extensions: [".js", ".jsx"],
    alias: { "./dist/cpexcel.js": "" }
  },
  entry: entries,
  output: {
    path: BUILD_PATH,
    filename: "js/[name].bundle.js"
  },
  module: {
    rules: [
      {
        test: /\.(js|jsx)$/,
        exclude(path) {
          return path.match(/node_modules/);
        },
        use: "babel-loader"
      },
      {
        test: /\.(css|sass|scss)$/,
        use: [
          MiniCssExtractPlugin.loader,
          {
            loader: "css-loader",
            options: {
              importLoaders: 2,
              sourceMap: true
            }
          },
          {
            loader: "postcss-loader",
            options: {
              plugins: () => [require("autoprefixer")],
              sourceMap: true
            }
          },
          {
            loader: "sass-loader",
            options: {
              sourceMap: true
            }
          }
        ]
      },
      {
        test: /\.woff2?(\?v=[0-9]\.[0-9]\.[0-9])?$/,
        use: {
          loader: "url-loader"
        }
      },
      {
        test: /\.(ttf|eot|svg)(\?[\s\S]+)?$/,
        use: "file-loader"
      }
    ]
  },
  plugins: [
    new MiniCssExtractPlugin({
      filename: "css/[name].bundle.css"
    })
  ]
};

module.exports = ({ mode = "development" }) => {
  const dev = require("./webpack.config.dev");
  const prod = require("./wepack.config.prod");
  return merge(
    { mode },
    config,
    mode === "production" ? prod.config : dev.config
  );
};
