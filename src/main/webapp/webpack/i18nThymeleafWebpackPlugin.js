/* eslint-disable @typescript-eslint/no-var-requires */
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

const path = require("path");
const Chunk = require("webpack/lib/Chunk.js");
const ConcatenatedModule = require("webpack/lib/optimize/ConcatenatedModule");
const { connectChunkGroupAndChunk } = require("webpack/lib/GraphHelpers");

/**
 * Generates a thymeleaf templated translations html file.
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
 * Checks that a requested javascript module is not an external
 * dependency.
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
    this.templatePath = this.options.templatePath || "../pages/templates/";
  }

  /**
   * Apply the compiler
   * @param {compiler} compiler the compiler instance
   * @returns {void}
   */
  apply(compiler) {
    let i18nsByRequests = {};
    const cache = compiler
      .getCache("i18nThymeleafWebpackPlugin")
      .getItemCache("i18nsByRequests", null);

    let cacheGetPromise;

    /**
     * Load the i18nsByRequests dictionary from the webpack cache.
     */
    compiler.hooks.beforeCompile.tap("i18nThymeleafWebpackPlugin", () => {
      if (!cacheGetPromise) {
        cacheGetPromise = cache.getPromise().then(
          (data) => {
            if (data) {
              i18nsByRequests = data;
            }
            return data;
          },
          (err) => {
            // Ignore error
          }
        );
      }
    });

    /**
     * Persist the i18nsByRequests dictionary to the webpack cache.
     */
    compiler.hooks.afterCompile.tapPromise(
      "i18nThymeleafWebpackPlugin",
      (compilation) => {
        if (compilation.compiler.isChild()) return Promise.resolve();
        return cacheGetPromise.then(async (oldData) => {
          // if we loaded data from cache, remove any entries that are no
          // longer required.
          if (oldData) {
            Object.keys(i18nsByRequests).forEach((moduleIdentifier) => {
              if (compilation.findModule(moduleIdentifier) === undefined) {
                delete i18nsByRequests[moduleIdentifier];
              }
            });
          }

          if (!oldData || oldData !== i18nsByRequests) {
            await cache.storePromise(i18nsByRequests);
          }
        });
      }
    );

    /**
     * Get a set of translation keys that are required by the ConcatenatedModule or any of its
     * dependencies.
     * @param {Compilation} compilation the Compilation object
     * @param {ConcatedModule} concatenatedModule the ConcatenatedModule to get translations keys from
     * @return {Set<string>} a set of the translations keys required by the concatenatedModule
     */
    const getKeysByConcatenatedModule = (compilation, concatenatedModule) => {
      let keys = new Set();
      const rootModule = concatenatedModule.rootModule;

      if (isValidLocalRequest(rootModule.identifier())) {
        if (i18nsByRequests[rootModule.identifier()]) {
          keys = new Set([
            ...keys,
            ...i18nsByRequests[rootModule.identifier()],
          ]);
        }

        for (const module of concatenatedModule.modules) {
          if (module instanceof ConcatenatedModule) {
            keys = new Set([
              ...keys,
              ...getKeysByConcatenatedModule(compilation, module),
            ]);
          } else {
            if (
              isValidLocalRequest(module.identifier()) &&
              i18nsByRequests[module.identifier()]
            ) {
              keys = new Set([
                ...keys,
                ...i18nsByRequests[module.identifier()],
              ]);
            }
          }
        }
      }

      return keys;
    };

    /**
     * Get a set of translation keys that are required by any of the dependencies
     * of the ChunkGroup.
     * @param {Compilation} compilation the Compilation object
     * @param {ChunkGroup} chunkGroup the ChunkGroup to get translations keys from
     * @return {Set<string>} a set of the translations keys required by the chunkGroup
     */
    const getKeysByChunkGroup = (compilation, chunkGroup) => {
      let keys = new Set();

      for (const chunk of chunkGroup.chunks) {
        compilation.chunkGraph
          .getChunkModulesIterable(chunk)
          .forEach((module) => {
            if (module instanceof ConcatenatedModule) {
              keys = new Set([
                ...keys,
                ...getKeysByConcatenatedModule(compilation, module),
              ]);
            } else {
              if (
                isValidLocalRequest(module.identifier()) &&
                i18nsByRequests[module.identifier()]
              ) {
                keys = new Set([
                  ...keys,
                  ...i18nsByRequests[module.identifier()],
                ]);
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
                  i18nsByRequests[parser.state.module.identifier()] =
                    i18nsByRequests[parser.state.module.identifier()] ||
                    new Set();
                  i18nsByRequests[parser.state.module.identifier()].add(key);
                }
              });
          });
      }
    );

    const { sources, Compilation } = require("webpack");

    /**
     * Generate thymeleaf templated translation files at the end of the
     * compilation
     */
    compiler.hooks.thisCompilation.tap(
      "i18nThymeleafWebpackPlugin",
      (compilation) => {
        /**
         * Delete entries from i18nsByRequests object before a module is built.
         * This removes old entries which were loaded from the cache and also
         * before a module is rebuilt (i.e. when running webpack with --watch
         * argument). This is done so that unnesecary i18n keys aren't present
         * in the generated html templates.
         */
        compilation.hooks.buildModule.tap(
          "i18nThymeleafWebpackPlugin",
          (module) => {
            delete i18nsByRequests[module.identifier()];
          }
        );

        /**
         * Emit a thymeleaf templated translations file for each entry that has
         * calls to i18n or has dependencies that have calls to i18n.
         */
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
                const filename = `i18n/${entrypointName}.html`;
                const newChunk = new Chunk(filename);
                newChunk.files = [filename];
                newChunk.ids = [];
                connectChunkGroupAndChunk(entrypoint, newChunk);

                const html = template(keys, entrypointName);
                compilation.emitAsset(
                  path.join(this.templatePath, filename),
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
