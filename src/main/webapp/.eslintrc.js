module.exports = {
  plugins: ["prettier"],
  parserOptions: {
    ecmaVersion: 2018,
    sourceType: "module"
  },
  env: {
    browser: true,
    node: true
  },
  rules: {
    "prettier/prettier": "error"
  }
};
