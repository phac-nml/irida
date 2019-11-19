const fs = require("fs");
const path = require("path");
const handlebars = require("handlebars");

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
    this.functionName = this.options.functionName || "i18n";
    this.entries = {};
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
                /*
                Make sure an argument was passed to the function.
                 */
                if (expr.arguments.length) {
                  const key = expr.arguments[0].value;
                  // console.log(key);
                  const entry =
                    reverseEntryPoints[
                      resolveEntry(parser.state.module, reverseEntryPoints)
                    ];
                  this.entries[entry] = this.entries[entry] || {};
                  this.entries[entry][key] = true;
                }
              });
          });
      }
    );

    /*
    Write the language files for each entry.
     */
    compiler.hooks.done.tap("i18nPropertiesWebpackPlugin", () => {
      const dir = path.join(compiler.options.output.path, "i18n");
      if (!fs.existsSync(dir)) {
        fs.mkdirSync(dir, { recursive: true });
      }

      fs.readFile(__dirname + "/i18n.html", "utf-8", (error, source) => {
        handlebars.registerHelper('tl', key => `/*[[#{${key}}]]*/ ""`);
        const template = handlebars.compile(source);

        Object.keys(this.entries).forEach(entry => {
          const keys = Object.keys(this.entries[entry]);
          const html = template({ keys });
          fs.writeFileSync(path.join(dir, `${entry}.html`), html);
          const entryPath = path.join(dir, `${entry}.html`);
          if (!fs.existsSync(path.dirname(entryPath))) {
            fs.mkdirSync(path.dirname(entryPath), { recursive: true });
          }
          fs.writeFileSync(entryPath, html);
        });
      });
    });
  }
}

module.exports = i18nPropertiesWebpackPlugin;
