const moment = require('moment');

/**
 * Create a DOM element that contains a "time since" string.  If it is not a valid
 * date that is passed, it will return the original value.
 * @param {string} data date to add to the string
 * @return {*} formatted date DOM or the initial value
 */
export function formatDateFromNowDOM({data}) {
  if (moment.isDate(new Date(data))) {
    const date = moment(new Date(data));
    return `
<time data-toggle="tooltip" data-placement="top" 
      title="${date.format("LLL")}">${date.fromNow()}</time>
  `;
  }
  return data;
}

/**
 * Generate the a human readable form from milliseconds.
 * @param {string} date to format
 * @return {string} humanized version of the date
 */
export function getHumanizedDuration({date}) {
  return moment.duration(date).humanize();
}

/**
 * Create a DOM element with an actual date.
 * @param {string} data date to format
 * @return {*} formatted date DOM of the initial value.
 */
export function formatDateDOM({data}) {
  if (moment.isDate(new Date(data))) {
    const date = moment(new Date(data));
    return `
<time>${date.format("LLL")}</time>    
`;
  }
  return data;
}
