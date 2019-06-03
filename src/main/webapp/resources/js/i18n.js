const getTranslation = key => {
  if (window.translations && window.translations[key])
    return window.translations[key];
  throw new Error(`No internationalization string for key: ${key}`);
};

const replaceKeys = (text, args) =>
  text.replace(/{([0-9]+)}/g, (_, index) => args[index]);

function i18n(key, ...args) {
  try {
    const text = getTranslation(key);
    return replaceKeys(text, args);
  } catch (e) {
    console.error(e);
    return `__${key}__`;
  }
}

/*
This is a special case for scripts that are not compiled by webpack.
 */
window.__i18n = i18n;
module.exports = i18n;
