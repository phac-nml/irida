const formatAntStyles = require("./styles");

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
