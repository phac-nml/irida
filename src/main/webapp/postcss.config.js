const formatAntStyles = require("./styles");

/**
 * Used by Post CSS through the WebPack build process
 */

/*
Import custom styles for Ant Design
 */
const modifyVars = formatAntStyles();

module.exports = {
  options: {
    lessOptions: {
      modifyVars,
      javascriptEnabled: true,
    },
  },
  plugins: [
    require("postcss-import"),
    require("autoprefixer"),
    require("postcss-nested"),
    require("postcss-preset-env")({
      browsers: "last 2 versions",
    }),
  ],
};
