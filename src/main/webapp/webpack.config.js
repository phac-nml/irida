// const path = require("path");
// const webpack = require("webpack");
// const merge = require("webpack-merge");
// const parts = require("./configs/webpack/webpack.parts");
// const entries = require("./configs/webpack/entries.js");
//
// const PATHS = {
//   build: path.resolve(__dirname, "resources/dist")
// };
//
// /*
//  This is used by both the production and the development configurations.
//  */
// const commonConfig = merge([
//   {
//     entry: entries,
//     stats: {
//       children: false,
//       cached: false
//     },
//

//     resolve: {
//       extensions: [".js", ".jsx"],
//       alias: { "./dist/cpexcel.js": "" }
//     },
//     module: {
//       rules: [
//         {
//           test: /\.woff2?(\?v=[0-9]\.[0-9]\.[0-9])?$/,
//           use: {
//             loader: "url-loader"
//           }
//         },
//         {
//           test: /\.(ttf|eot|svg)(\?[\s\S]+)?$/,
//           use: "file-loader"
//         }
//       ]
//     }
//   },
//   parts.loadJavaScript(),
//   parts.loadCSS()
// ]);
//
// /* ======================
//  PRODUCTION CONFIGURATION
// ====================== */
// const productionConfig = merge([
//   {
//     plugins: [
//       new webpack.DefinePlugin({
//         "process.env.NODE_ENV": JSON.stringify("production")
//       })
//     ]
//   },
//   parts.progressBar(),
//   parts.compressJavaScript(),
//   parts.clean([PATHS.build])
// ]);
//
// /* =======================
//  DEVELOPMENT CONFIGURATION
//  ====================== */
// const developmentConfig = merge([
//   {
//     devtool: "inline-source-map"
//   }
// ]);
//
// module.exports = env => {
//   if (env.target === "production") {
//     return merge(commonConfig, productionConfig);
//   }
//
//   return merge(commonConfig, developmentConfig);
// };

const path = require("path");
const webpack = require("webpack");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const CleanWebpackPlugin = require("clean-webpack-plugin");
const UglifyWebpackPlugin = require("uglifyjs-webpack-plugin");

const entries = require("./configs/webpack/entries.js");

const BUILD_PATH = path.resolve(__dirname, "resources/dist");
module.exports = {
  externals: {
    jquery: "jQuery",
    angular: "angular",
    moment: "moment"
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
      // {
      //   test: /\.js$/,
      //   enforce: "pre", // "post" too
      //   use: "eslint-loader",
      // },
      {
        test: /\.(js|jsx)$/,
        exclude(path) {
          return path.match(/node_modules/);
        },
        use: "babel-loader"
      },
      {
        test: /\.css$/,
        use: [MiniCssExtractPlugin.loader].concat("css-loader")
      },
      {
        test: /\.scss$/,
        use: ["style-loader", "css-loader", "sass-loader"]
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
  optimization: {
    minimizer: [new UglifyWebpackPlugin({ sourceMap: true })]
    // splitChunks: {
    //   chunks: "initial"
    // }
  },
  plugins: [
    new MiniCssExtractPlugin({
      filename: "css/[name].bundle.css"
    }),
    new CleanWebpackPlugin([BUILD_PATH])
  ]
};
