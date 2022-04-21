/**
 * Used by Post CSS through the WebPack build process
 */

module.exports = {
  plugins: [
    require("postcss-import"),
    require("autoprefixer"),
    require("postcss-nested"),
    require("postcss-preset-env")({
      browsers: "last 2 versions",
    }),
  ],
};
