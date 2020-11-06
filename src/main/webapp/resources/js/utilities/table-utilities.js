import { getUserLocale } from "./user-utilities";

export const stringSorter = (property) => (a, b) =>
  a[property].localeCompare(b[property], getUserLocale(), {
    sensitivity: "base",
  });
