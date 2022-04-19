console.log(process.env.BABEL_ENV);
console.log(process.env.BABEL_ENV === "development");

module.exports = {
  presets: [
    "@babel/preset-env",
    [
      "@babel/preset-react",
      {
        development: process.env.BABEL_ENV === "development",
      }
    ]
  ],
  "plugins": [
    [
      "import",
      {
        "libraryName": "antd",
        "libraryDirectory": "lib",
        "style": true
      }
    ],
    "@babel/plugin-proposal-export-default-from"
  ]
};
