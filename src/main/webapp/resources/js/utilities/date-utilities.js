import dayjs from "dayjs";
import relativeTime from "dayjs/plugin/relativeTime";
import duration from "dayjs/plugin/duration";

export function formatInternationalizedDateTime(
  date,
  options = {
    hour: "numeric",
    minute: "numeric",
    year: "numeric",
    month: "short",
    day: "numeric",
  }
) {
  if (!isDate(date)) return "";
  return new Intl.DateTimeFormat(window.TL.LANGUAGE_TAG, options).format(
    new Date(date)
  );
}

/**
 * Generate the a human readable form from milliseconds.
 * @param {Number} date to format
 * @return {string} humanized version of the date
 */
export function getHumanizedDuration({ date }) {
  dayjs.extend(duration);
  dayjs.extend(relativeTime);
  return dayjs.duration(-date).humanize(false);
}

/**
 * Generate the time from now.  Renders as human readable.s
 * @param {number} date raw string date from server
 * @return {*}
 */
export function fromNow({ date }) {
  // Using dayjs because Intl.RelativeTimeFormat does not have browser
  // support in IE11 or safari yet.
  dayjs.extend(duration);
  dayjs.extend(relativeTime);
  return dayjs.duration(dayjs(date).diff(dayjs())).humanize(true);
}

/**
 * Format unix timestamp as human readable string.
 * @param  {(string | number)} date unix timestamp
 * @param {String} format defaults to "lll" which is mmm dd, YYYY h:mm AM
 * @return {string} formatted date
 */
export function formatDate({ date, format }) {
  return formatInternationalizedDateTime(date, format);
}

/**
 * Utility function to determine if a string is a date.
 * @param {(number | string)} date
 * @returns {boolean}
 */
export const isDate = (date) => !isNaN(new Date(date).valueOf());

// get a readable string of the time from a given number of seconds
export function getDurationFromSeconds(seconds) {
  dayjs.extend(duration);
  dayjs.extend(relativeTime);
  return dayjs.duration(seconds * 1000).humanize(false);
}
