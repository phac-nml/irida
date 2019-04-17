const emailRegex = /[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?/;

/**
 * Ensure that an email address is formatted correctly.
 * This regular expression is taken from (https://stackoverflow.com/questions/46155/how-to-validate-an-email-address-in-javascript)
 * @param {string} email address to validate
 * @returns {boolean}
 */
export const validateEmail = email =>
  emailRegex.test(String(email).toLowerCase());
