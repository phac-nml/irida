export const getI18N = term => {
  const { i18n } = window.PAGE;
  if (typeof i18n === "undefined") {
    throw "No internationalisation's available on the current page";
  } else if (typeof i18n[term] !== "string") {
    throw `Need a string value to translate, not: [${term}]`;
  }
  return i18n[term];
};
