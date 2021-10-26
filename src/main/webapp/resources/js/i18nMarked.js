import marked from "marked";

export function i18nMarked(key, ...args) {
  return marked(i18n(key, ...args));
}