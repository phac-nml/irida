export const stringSorter = (property) => (a, b) =>
  a[property].localeCompare(b[property], window.TL.LANGUAGE_TAG, {
    sensitivity: "base",
  });
