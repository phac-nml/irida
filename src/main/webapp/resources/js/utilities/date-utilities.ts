import dayjs from "dayjs";
import relativeTime from "dayjs/plugin/relativeTime";
import duration from "dayjs/plugin/duration";

declare let window: IridaWindow;

export function formatInternationalizedDateTime(
  date: number | string | Date,
  options: Intl.DateTimeFormatOptions = {
    hour: "numeric",
    minute: "numeric",
    year: "numeric",
    month: "short",
    day: "numeric",
  }
) {
  if (!isDate(date)) return "";
  const LOCALE = window.TL?.LANGUAGE_TAG || "en";
  return new Intl.DateTimeFormat(LOCALE, options).format(new Date(date));
}

/**
 * Generate the a human readable form from milliseconds.
 * @param {Number} date to format
 * @return {string} humanized version of the date
 */
export function getHumanizedDuration({ date }: { date: string | number }) {
  dayjs.extend(duration);
  dayjs.extend(relativeTime);
  return dayjs.duration(-date).humanize(false);
}

/**
 * Generate the time from now.  Renders as human readable.s
 * @param {number} date raw string date from server
 * @return {*}
 */
export function fromNow({ date }: { date: string | number }) {
  // Using dayjs because Intl.RelativeTimeFormat does not have browser
  // support in IE11 or safari yet.
  dayjs.extend(duration);
  dayjs.extend(relativeTime);
  return dayjs.duration(dayjs(date).diff(dayjs())).humanize(true);
}

/**
 * Format unix timestamp as human readable string.
 * @param  date unix timestamp
 * @param  format defaults to "lll" which is mmm dd, yyyy h:mm AM
 * @return formatted date
 */
export function formatDate({
  date,
  format,
}: {
  date: Date;
  format?: Intl.DateTimeFormatOptions;
}) {
  return formatInternationalizedDateTime(date, format);
}

/**
 * Utility function to determine if a string is a date.
 * @param  date
 * @returns true if Date
 */
export const isDate = (date: number | string | Date) =>
  !isNaN(new Date(date).valueOf());

// get a readable string of the time from a given number of seconds
export function getDurationFromSeconds(seconds: number) {
  dayjs.extend(duration);
  dayjs.extend(relativeTime);
  return dayjs.duration(seconds * 1000).humanize(false);
}
