import { validateSampleName } from "../apis/projects/samples";

const emailRegex =
  /[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?/;

/**
 * Ensure that an email address is formatted correctly.
 * This regular expression is taken from (https://stackoverflow.com/questions/46155/how-to-validate-an-email-address-in-javascript)
 * @param {string} email address to validate
 * @returns {boolean}
 */
export const validateEmail = (email) =>
  emailRegex.test(String(email).toLowerCase());

/**
 * Server validate a sample name asynchronously
 * @param {string} name - Sample name to validate
 * @returns {Promise<void>}
 */
export const serverValidateSampleName = async (name) => {
  const data = await validateSampleName(name);
  if (data.status === "success") {
    return Promise.resolve();
  } else {
    return Promise.reject(new Error(data.help));
  }
};
