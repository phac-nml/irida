module.exports = {
  plugins: ["prettier"],
  parserOptions: {
    ecmaVersion: 6,
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
