/**
 * Internationalize numbers.  (e.g. 5000 --> 5,000)
 * @param {number} number
 * @return {string}
 */
import { getUserLocale } from "./user-utilities";

export function formatNumber(number) {
  return new Intl.NumberFormat(getUserLocale()).format(number);
}
