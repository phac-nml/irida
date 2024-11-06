/**
 * @file This is part of the IRIDA internationalization system, designed to
 * allow JavaScript files to have keys to translation strings found in the
 * messages_[lang].properties files.
 *
 * When the JavaScript files are bundled using webpack, a webpack preprocessor
 * will look through the file and find all calls to the function `i18n(*)`, and
 * capture the arguments. Webpack then creates a Thymeleaf template with these
 * arguments in a JavaScript object syntax where the key is the argument and
 * the value is a Thymeleaf expression to be converted by Thymeleaf using
 * Springs messages service.
 *
 * When the page is processed by the browser, the calls the the i18n calls
 * are immediately invoked which calls the i18n function in this file.  This
 * will either get the value from `window.translations` or log and error to
 * the console and display `__[argument]__` in the browser where the text was
 * expected.
 */

/**
 * Get the translation for the key from the global window.translations
 * @param {string} key
 * @returns {*}
 */
const getTranslation = (key) => {
  const translations = Object.assign({}, ...window.translations);
  if (translations && translations[key]) return translations[key];
  throw new Error(`No internationalization string for key: ${key}`);
};

/**
 * Replace placeholders in the translation with provided values.
 * @param {string} text
 * @param {array} args - value to inject into the placeholder in the translation
 * @returns {*}
 */
const replaceKeys = (text, args) =>
  text.replace(/{([0-9]+)}/g, (_, index) => args[index]);

/**
 * Globally exposed function to allow to internationalization.
 * @param {string} key
 * @param {array} args - list of strings to inject into the translation.
 * @returns {string}
 */
function i18n(key, ...args) {
  try {
    const text = getTranslation(key);
    return replaceKeys(text, args);
  } catch (e) {
    console.error(e);
    return `__${key}__`;
  }
}

module.exports = i18n;
