module.exports = {
  env: {
    node: "true",
    browser: true,
    es2021: true,
  },
  globals: {
    i18n: true,
  },
  extends: [
    "eslint:recommended",
    "plugin:react/recommended",
    "plugin:react-hooks/recommended",
    "prettier",
  ],
  parserOptions: {
    ecmaFeatures: {
      jsx: true,
    },
    ecmaVersion: 12,
    sourceType: "module",
  },
  plugins: ["react", "jsx-a11y"],
  rules: { "react/prop-types": 0 },
};
