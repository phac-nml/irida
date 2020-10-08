/**
 * Internationalize numbers.  (e.g. 5000 --> 5,000)
 * @param {number} number
 * @return {string}
 */
export function formatNumber(number) {
  return new Intl.NumberFormat(window.TL.LANGUAGE_TAG || "en-CA").format(
    number
  );
}
