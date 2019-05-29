const path = require("path");
const merge = require("webpack-merge");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const I18nPlugin = require("i18n-webpack-plugin");
const properties = require('properties');
const fs = require('fs');
const glob = require('glob');

const entries = require("./entries.js");

const BUILD_PATH = path.resolve(__dirname, "dist");

parse_messages = function(source) {
  let $this = this;

  let options = {
    namespaces: false,
  }

  var messages_source = fs.readFileSync(source, {encoding: "utf-8"});

  return properties.parse(messages_source, options);
}

const message_files = glob.sync("../resources/i18n/messages_*.properties");
let languages = {};

for (i=0; i<message_files.length; i++) {
  const message_file = message_files[i];
  const locale = message_file.match(/messages_(.*)\.properties/).pop();
  languages[locale] = parse_messages(message_file);
}

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
    publicPath: `/dist/`,
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
          "css-loader",
          {
            loader: "postcss-loader",
            options: {
              plugins: () => [require("autoprefixer")],
              sourceMap: true
            }
          },
          "sass-loader"
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
      },
      {
        test: require.resolve("jquery"),
        use: [
          {
            loader: "expose-loader",
            options: "$"
          },
          {
            loader: "expose-loader",
            options: "jQuery"
          }
        ]
      },
      {
        test: require.resolve("angular"),
        use: [
          {
            loader: "expose-loader",
            options: "angular"
          }
        ]
      }
    ]
  },
  plugins: [
    new MiniCssExtractPlugin({
      filename: "css/[name].bundle.css"
    }),
  ]
};

const dev = require("./webpack.config.dev");
const prod = require("./webpack.config.prod");

module.exports = ({ mode = "development "}) => {
  if (mode === "development") {
    return merge(
      config,
      {
        output: {
          filename: (chunkData) => {
            return chunkData.chunk.name === 'vendor' ? 'js/[name].bundle.js' : `js/[name].en.bundle.js`;
          },
          chunkFilename: `js/[name].en.bundle.js`,
        },
        plugins: [
          new I18nPlugin(languages["en"])
        ],
      },
      dev.config
    )
  } else {
    return Object.keys(languages).map(function(language) {
      return merge(
        config,
        {
          output: {
            filename: (chunkData) => {
              return chunkData.chunk.name === 'vendor' ? 'js/[name].bundle.js' : `js/[name].${language}.bundle.js`;
            },
            chunkFilename: `js/[name].en.bundle.js`,
          },
          plugins: [
            new I18nPlugin(languages[language])
          ],
        },
        prod.config
      );
    });
  }
}

