module.exports = {
  "extends": [
    "plugin:react/recommended",
    "prettier",
    "prettier/react",
  ],
  "plugins": [
    "prettier",
    "react"
  ],
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
