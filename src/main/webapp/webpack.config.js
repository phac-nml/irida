const path = require("path");
const webpack = require("webpack");
const merge = require("webpack-merge");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const i18nThymeleafWebpackPlugin = require("./webpack/i18nThymeleafWebpackPlugin");

const dev = require("./webpack.config.dev");
const prod = require("./webpack.config.prod");

const entries = require("./entries.js");

const BUILD_PATH = path.resolve(__dirname, "dist");

const config = {
  externals: {
    jquery: "jQuery",
    angular: "angular",
  },
  stats: {
    children: false,
    cached: false,
  },
  resolve: {
    extensions: [".js", ".jsx"],
    alias: {
      "./dist/cpexcel.js": "",
    },
  },
  entry: entries,
  output: {
    path: BUILD_PATH,
    publicPath: `/dist/`,
    filename: "js/[name].bundle.js",
  },
  module: {
    rules: [
      {
        test: /\.(js|jsx)$/,
        exclude: /(node_modules)/,
        use: "babel-loader?cacheDirectory",
      },
      {
        test: /\.css$/,
        use: [
          MiniCssExtractPlugin.loader,
          "css-loader",
          {
            loader: "postcss-loader",
            options: {
              ident: "postcss",
              plugins: (loader) => [
                require("postcss-import")({ root: loader.resourcePath }),
                require("postcss-preset-env")(),
                require("cssnano")(),
                require("autoprefixer")(),
                require("postcss-nested")(),
              ],
            },
          },
        ],
      },
      {
        test: /\.woff2?(\?v=[0-9]\.[0-9]\.[0-9])?$/,
        use: {
          loader: "url-loader",
        },
      },
      {
        test: /\.(ttf|eot|svg|otf|gif|png)(\?v=[0-9]\.[0-9]\.[0-9])?$/,
        use: {
          loader: "file-loader",
        },
      },
      {
        test: require.resolve("jquery"),
        use: [
          {
            loader: "expose-loader",
            options: "$",
          },
          {
            loader: "expose-loader",
            options: "jQuery",
          },
        ],
      },
      {
        test: require.resolve("angular"),
        use: [
          {
            loader: "expose-loader",
            options: "angular",
          },
        ],
      },
    ],
  },
  plugins: [
    new MiniCssExtractPlugin({
      filename: "css/[name].bundle.css",
    }),
    new i18nThymeleafWebpackPlugin({
      functionName: "i18n",
    }),
    new webpack.ProvidePlugin({
      i18n: path.resolve(path.join(__dirname, "resources/js/i18n")),
    }),
  ],
};

module.exports = ({ mode = "development" }) => {
  return merge(
    { mode },
    config,
    mode === "production" ? prod.config : dev.config
  );
};
