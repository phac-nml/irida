module.exports = {
  options: {
    lessOptions: {
      // modifyVars: { ...formatAntStyles() },
      javascriptEnabled: true,
    },
  },
  plugins: [
      require('autoprefixer'),
      require('postcss-nested'),
      require('postcss-preset-env')({
        browsers: 'last 2 versions',
      }),
  ],
};
