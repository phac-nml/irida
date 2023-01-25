module.exports = {
  env: {
    node: "true",
    browser: true,
    es2021: true,
    jest: true,
  },
  globals: {
    i18n: true,
    __webpack_public_path__: true,
  },
  extends: ["airbnb", "airbnb-typescript", "plugin:prettier/recommended"],
  // [
  //   "airbnb-typescript",
  //   "eslint:recommended",
  //   "plugin:react/recommended",
  //   "plugin:react-hooks/recommended",
  //   "plugin:@typescript-eslint/eslint-recommended",
  //   "plugin:@typescript-eslint/recommended",
  //   "plugin:prettier/recommended",
  // ],
  parserOptions: {
    project: "./tsconfig.json",
    ecmaFeatures: {
      jsx: true,
    },
    ecmaVersion: 12,
    sourceType: "module",
  },
  plugins: ["react", "jsx-a11y", "@typescript-eslint"],
  settings: {
    react: {
      version: "detect",
    },
  },
  rules: {
    "@typescript-eslint/explicit-function-return-type": "warn",
    "react/prop-types": 0,
    "prefer-destructuring": [
      "error",
      {
        array: true,
        object: true,
      },
      {
        enforceForRenamedProperties: false,
      },
    ],
    "prettier/prettier": [
      "error",
      {
        endOfLine: "auto",
      },
    ],
  },
};
