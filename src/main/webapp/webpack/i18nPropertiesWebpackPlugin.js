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
    this.locales = [];
    this.translations = message_files.reduce((hash, file) => {
      const locale = file.match(/messages_(.*)\.properties/).pop();
      this.locales.push(locale);
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
                const key = expr.arguments[0].value;
                const entry =
                  reverseEntryPoints[
                    resolveEntry(parser.state.module, reverseEntryPoints)
                  ];
                this.entryTranslations[entry] =
                  this.entryTranslations[entry] || {};
                // this.entryTranslations[entry][value] = this.translations.en[value];
                this.locales.forEach(locale => {
                  this.entryTranslations[entry][locale] =
                    this.entryTranslations[entry][locale] || {};
                  this.entryTranslations[entry][locale][
                    key
                  ] = this.translations[locale][key];
                });
              });
          });
      }
    );

    compiler.hooks.done.tap("i18nPropertiesWebpackPlugin", () => {
      const dir = "./dist/i18n";
      if(!fs.existsSync(dir)) {
        fs.mkdirSync(dir);
      }

      Object.keys(this.entryTranslations).forEach(entry => {
        // Loop evey each language
        Object.keys(this.entryTranslations[entry]).forEach(lang => {
          fs.writeFileSync(
            `./dist/i18n/${entry}.${lang}.json`,
            JSON.stringify(this.entryTranslations[entry][lang], null, 2)
          );
        });
      });

      console.log("Done translations");
    });
  }
}

module.exports = i18nPropertiesWebpackPlugin;
