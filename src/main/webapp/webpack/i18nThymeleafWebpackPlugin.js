/**
 * @file Part of the IRIDA internationalization system.
 *
 * This webpack plugin is responsible for looking through all entries and
 * finding calls to the function `i18n`.  The arguments for these calls are
 * gathered and a Thymeleaf templates are generated for each entry containing
 * a JavaScript object where the keys are the arguments and the values
 * are formatted for Thymeleaf to internationalize.
 */

"use strict";

const ConcatenatedModule = require('webpack/lib/optimize/ConcatenatedModule');

/**
 * @param {string[]} keys the translation keys required for the entry
 * @param {string} entry the name of the entry
 * @returns {string} the generated html
 */
const template = (keys, entry) => `
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="en">
  <body>
    <script id="${entry.replace(
      "/",
      "-"
    )}-translations" th:inline="javascript" th:fragment="i18n">
      window.translations = window.translations || [];
      window.translations.unshift({
        ${keys.map((key) => `"${key}": /*[[#{${key}}]]*/ ""`)}
      });
    </script>
  </body>
</html>
`;

/**
 * @param {string} request the path to the js file being requested
 * @returns {boolean} request is valid and is local
 */
const isValidLocalRequest = (request) => {
  return (
    typeof request !== "undefined" &&
    request.match(/src\/main\/webapp\/resources\/js/)
  );
};

class i18nThymeleafWebpackPlugin {
  constructor(options) {
    this.options = options || {};
    this.functionName = this.options.functionName || "i18n";
  }

  /**
   * @param {compiler} compiler the compiler instance
   * @returns {void}
   */
  apply(compiler) {
    let i18nsByRequests = {};

    /**
     * @param {Compilation} compilation the Compilation object
     * @param {ConcatedModule} concatenatedModule the ConcatenatedModule to get translations keys from
     * @return {Set<string>} a set of the translations keys required by the concatenatedModule
     */
    const getKeysByConcatenatedModule = (compilation, concatenatedModule) => {
      let keys = new Set();

      if (
        isValidLocalRequest(concatenatedModule.rootModule.userRequest)
      ) {
        if ( i18nsByRequests[module.userRequest] ) {
          keys = new Set([...keys, ...i18nsByRequests[concatenatedModule.rootModule.userRequest]]);
        }

        for (const module of concatenatedModule.modules) {
          if (module instanceof ConcatenatedModule) {
            keys = new Set([...keys, ...getKeysByConcatenatedModule(compilation, module)]);
          }
          else {
            if (
              isValidLocalRequest(module.userRequest) &&
              i18nsByRequests[module.userRequest]
            ) {
              keys = new Set([...keys, ...i18nsByRequests[module.userRequest]]);
            }
          }
        }
      }

      return keys;
    }

    /**
     * @param {Compilation} compilation the Compilation object
     * @param {ChunkGroup} chunkGroup the ChunkGroup to get translations keys from
     * @return {Set<string>} a set of the translations keys required by the chunkGroup
     */
    const getKeysByChunkGroup = (compilation, chunkGroup) => {
      let keys = new Set();

      for (const chunk of chunkGroup.chunks) {
        compilation.chunkGraph.getChunkModulesIterable(chunk).forEach(module => {
          if ( module instanceof ConcatenatedModule ) {
            keys = new Set([...keys, ...getKeysByConcatenatedModule(compilation, module)]);
          }
          else {
            if (
              isValidLocalRequest(module.userRequest) &&
              i18nsByRequests[module.userRequest]
            ) {
              keys = new Set([...keys, ...i18nsByRequests[module.userRequest]]);
            }
          }
        });
      }

      const childKeys = chunkGroup
        .getChildren()
        .map((child) => [...getKeysByChunkGroup(compilation, child)]);

      return new Set([...keys, ...childKeys.flat()]);
    };

    /**
     * Gather all the translation keys required by each js file.
     */
    compiler.hooks.normalModuleFactory.tap(
      "i18nThymeleafWebpackPlugin",
      (factory) => {
        factory.hooks.parser
          .for("javascript/auto")
          .tap("i18nThymeleafWebpackPlugin", (parser) => {
            parser.hooks.call
              .for(this.functionName)
              .tap("i18nThymeleafWebpackPlugin", (expr) => {
                /*
                Make sure an argument was passed to the function.
                 */
                if (expr.arguments.length) {
                  const key = expr.arguments[0].value;
                  i18nsByRequests[parser.state.module.userRequest] =
                    i18nsByRequests[parser.state.module.userRequest] ||
                    new Set();
                  i18nsByRequests[parser.state.module.userRequest].add(key);
                }
              });
          });
      }
    );

    /**
     * Delete entries from i18nByRequests object when a js file is updated.
     * This is done so that once a i18n call is removed from a file it will delete it from the object.
     */
    compiler.hooks.watchRun.tap(
      "i18nThymeleafWebpackPlugin",
      (compiler, err) => {
        const { watchFileSystem } = compiler;
        const watcher = watchFileSystem.watcher || watchFileSystem.wfs.watcher;

        for (const file of Object.keys(watcher.mtimes)) {
          delete i18nsByRequests[file];
        }
      }
    );

    const { sources, Compilation } = require('webpack');

    /**
     * Emit a thymeleaf templated translations file for each entry that has calls to i18n or has dependencies
     * that have calls to i18n.
    */
    compiler.hooks.thisCompilation.tap(
      "i18nThymeleafWebpackPlugin",
      (compilation) => {
        compilation.hooks.processAssets.tapPromise(
          {
            name: "i18nThymeleafWebpackPlugin",
            state: Compilation.PROCESS_ASSETS_STAGE_ADDITIONAL,
          },
          async () => {

            for (const [
              entrypointName,
              entrypoint,
            ] of compilation.entrypoints.entries()) {
              const keys = [...getKeysByChunkGroup(compilation, entrypoint)];

              if (keys.length) {
                /*
                This adds a file for translations for webpack to write to the file system.
                */
                const html = template(keys, entrypointName);
                compilation.emitAsset(
                  `../pages/templates/i18n/${entrypointName}.html`,
                  new sources.RawSource(html)
                );
              }
            }
          }
        );
      }
    );
  }
}

module.exports = i18nThymeleafWebpackPlugin;
