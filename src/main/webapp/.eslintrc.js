module.exports = {
  extends: "google",
  installedESLint: true,
  env: {
    browser: true
  },
  rules: {
    "new-cap": ["error", { capIsNewExceptions: ["DataTable"] }],
    "max-len": [
      "error",
      {
        ignoreStrings: true,
        ignoreComments: true
      }
    ],
  }
};
