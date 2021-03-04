const path = require("path");
const webpack = require("webpack");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const CssMinimizerPlugin = require("css-minimizer-webpack-plugin");
const TerserPlugin = require("terser-webpack-plugin");
const i18nThymeleafWebpackPlugin = require("./webpack/i18nThymeleafWebpackPlugin");
const entries = require("./entries");

const webpackConfig = {
  entry: entries,
  resolve: {
    symlinks: false,
  },
  output: {
    filename: "js/[name].bundle.js",
    path: path.resolve(__dirname, "dist"),
    pathinfo: false,
  },
  module: {
    rules: [
      {
        test: /\.(js|jsx)$/i,
        include: path.resolve(__dirname, "resources/js"),
        loader: "babel-loader",
        options: {
          cacheCompression: false,
          cacheDirectory: true,
        },
      },
      {
        test: /\.less$/i,
        use: [
          MiniCssExtractPlugin.loader,
          "css-loader",
          {
            loader: "less-loader",
            options: {
              lessOptions: {
                javascriptEnabled: true,
              },
            },
          },
        ],
      },
      {
        test: /\.css$/,
        use: [
          MiniCssExtractPlugin.loader,
          { loader: "css-loader", options: { importLoaders: 1 } },
          "postcss-loader",
        ],
      },
      {
        test: /\.(png|svg|jpg|jpeg|gif)$/i,
        type: "asset/resource",
      },
      {
        test: /\.(woff|woff2|eot|ttf|otf)$/i,
        type: "asset/resource",
      },
    ],
  },
  resolve: {
    extensions: [".js", ".jsx"],
  },
  plugins: [
    new MiniCssExtractPlugin({
      ignoreOrder: true,
      filename: "css/[name].bundle.css",
    }),
    new i18nThymeleafWebpackPlugin({
      functionName: "i18n",
    }),
    new webpack.ProvidePlugin({
      i18n: path.resolve(path.join(__dirname, "resources/js/i18n")),
      process: "process/browser",
    }),
  ],
};

module.exports = (env, argv) => {
  if (argv.mode === "development") {
    webpackConfig.devtool = "source-map";
    webpackConfig.optimization = {
      minimize: false,
    };
  }

  if (argv.mode === "production") {
    webpackConfig.optimization = {
      minimize: true,
      minimizer: [
        new CssMinimizerPlugin({ parallel: true }),
        new TerserPlugin({ parallel: true, include: /\/resources/ }),
      ],
    };
  }

  return webpackConfig;
};
