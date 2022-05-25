import { validateSampleName } from "../apis/projects/samples";

const emailRegex =
  /[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?/;

const minimumPasswordLength = 8;

/*
  Regex checks for:
  1 uppercase letter
  1 lowercase letter
  1 number
  1 symbol
  Minimum of 8 characters
 */
const passwordRegex = new RegExp(
  "^(?=.*\\d)(?=.*[!@#$%^&*()+?/<>={}.\\\\])(?=.*[a-z])(?=.*[A-Z]).{" +
    minimumPasswordLength +
    ",}$",
  ""
);

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

/**
 * Validate a password
 * @param {string} password - Password to validate
 * @returns {Promise<void>}
 */
export const validatePassword = (password) => {
  if (password.length !== 0) {
    if (password.length >= minimumPasswordLength) {
      if (passwordRegex.test(password)) {
        return Promise.resolve();
      } else {
        return Promise.reject(i18n("PasswordReset.input.passwordNotMatch"));
      }
    }
    return Promise.reject(i18n("PasswordReset.input.minLength"));
  } else {
    return Promise.reject(i18n("PasswordReset.passwordIsRequired"));
  }
};
