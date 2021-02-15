const formatAntStyles = require("./styles");

module.exports = {
  options: {
    lessOptions: {
      modifyVars: { ...formatAntStyles() },
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
// plugins: (loader) => [
//   require("postcss-import")({ root: loader.resourcePath }),
//   require("postcss-preset-env")(),
//   require("cssnano")(),
//   require("autoprefixer")(),
//   require("postcss-nested")(),
// ],
