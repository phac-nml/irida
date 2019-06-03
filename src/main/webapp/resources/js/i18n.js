function getTranslation(key) {
  if (window.translations && window.translations[key])
    return window.translations[key];
  throw new Error(`No internationalization string for key: ${key}`);
}

function i18n(key) {
  try {
    return getTranslation(key);
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
