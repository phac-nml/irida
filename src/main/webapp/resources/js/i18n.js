/**
 * Get the translation for the key from the global window.translations
 * @param {string} key
 * @returns {*}
 */
const getTranslation = key => {
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
