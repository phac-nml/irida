const fs = require("fs");
const glob = require("glob");
const properties = require("properties");
const { RawSource } = require("webpack-sources");

const parse_messages = source => {
  const messages_source = fs.readFileSync(source, { encoding: "utf-8" });
  return properties.parse(messages_source, { namespaces: false });
};

module.exports = class i18nPropertiesPlugin {
  constructor(options) {
    this.options = options;
  }
  apply(compiler) {
    compiler.plugin("emit", (compilation, cb) => {
      const message_files = glob.sync(
        "../resources/i18n/messages_*.properties"
      );
      message_files.forEach(file => {
        const locale = file.match(/messages_(.*)\.properties/).pop();
        const json = parse_messages(file);
        compilation.assets[`lang/i18n.${locale}.js`] = new RawSource(
          `window.translations = ${JSON.stringify(json)}`
        );
      });
      cb();
    });
  }
};
