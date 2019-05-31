const fs = require("fs");
const glob = require("glob");
const properties = require("properties");
const { RawSource } = require("webpack-sources");

const parse_messages = source => {
  const messages_source = fs.readFileSync(source, { encoding: "utf-8" });
  return properties.parse(messages_source, { namespaces: false });
};

// resolve entry for given module, we try to exit early with rawRequest in case of multiple modules issuing request
function resolveEntry(module, reverseEntryPoints) {
  let issuer = module;
  if (reverseEntryPoints[issuer.rawRequest]) {
    return issuer.rawRequest;
  }
  while (issuer.issuer) {
    issuer = issuer.issuer;
    if (reverseEntryPoints[issuer.rawRequest]) {
      return issuer.rawRequest;
    }
  }
  return issuer.rawRequest;
}

class i18nPropertiesWebpackPlugin {
  constructor(options) {
    this.options = options || {};

    const message_files = glob.sync("../resources/i18n/messages_*.properties");
    this.translations = message_files.reduce((hash, file) => {
      const locale = file.match(/messages_(.*)\.properties/).pop();
      hash[locale] = parse_messages(file);
      return hash;
    }, {});

    this.functionName = this.options.functionName || "i18n";
    this.entryTranslations = {};
  }

  apply(compiler) {
    let entryPoints = {};
    let reverseEntryPoints = {};

    compiler.hooks.compilation.tap(
      "i18nPropertiesWebpackPlugin",
      compilation => {
        entryPoints = compilation.options.entry;

        if (typeof entryPoints === "string" || Array.isArray(entryPoints)) {
          entryPoints = { main: entryPoints };
        }

        // prepare reverseEntryPoints object for entry resolution of given module
        reverseEntryPoints = Object.keys(entryPoints).reduce(
          (reverseEntryPointsAcc, name) => {
            let entryPoint = entryPoints[name];
            if (!Array.isArray(entryPoint)) {
              entryPoint = [entryPoint];
            }
            entryPoint.reduce((acc, curr) => {
              acc[curr] = name;
              return acc;
            }, reverseEntryPointsAcc);
            return reverseEntryPointsAcc;
          },
          {}
        );
      }
    );

    compiler.hooks.normalModuleFactory.tap(
      "i18nPropertiesWebpackPlugin",
      factory => {
        factory.hooks.parser
          .for("javascript/auto")
          .tap("i18nPropertiesWebpackPlugin", parser => {
            parser.hooks.call
              .for(this.functionName)
              .tap("i18nPropertiesWebpackPlugin", expr => {
                const value = expr.arguments[0].value;
                const entry =
                  reverseEntryPoints[
                    resolveEntry(parser.state.module, reverseEntryPoints)
                  ];
                this.entryTranslations[entry] =
                  this.entryTranslations[entry] || {};
                this.entryTranslations[entry][value] = this.translations.en[value];
              });
          });
      }
    );

    compiler.hooks.done.tap("i18nPropertiesWebpackPlugin", () => {
      console.log(this.entryTranslations);
    });

    // compiler.plugin("emit", (compilation, cb) => {
    //   message_files.forEach(file => {
    //     const locale = file.match(/messages_(.*)\.properties/).pop();
    //     const json = parse_messages(file);
    //     compilation.assets[`lang/i18n.${locale}.js`] = new RawSource(
    //       `window.translations = ${JSON.stringify(json)}`
    //     );
    //   });
    //   cb();
    // });
  }
}

module.exports = i18nPropertiesWebpackPlugin;
