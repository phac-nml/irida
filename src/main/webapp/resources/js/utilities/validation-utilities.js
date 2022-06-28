import { validateSampleName } from "../apis/projects/samples";

const emailRegex =
  /[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?/;

export const usernameRuleList = [
  {
    required: true,
    message: i18n("validation-utilities.username.required"),
  },
  {
    min: 3,
    message: i18n("validation-utilities.username.min"),
  },
];

export const firstNameRuleList = [
  {
    required: true,
    message: i18n("validation-utilities.firstName.required"),
  },
  {
    min: 2,
    message: i18n("validation-utilities.firstName.min"),
  },
];

export const lastNameRuleList = [
  {
    required: true,
    message: i18n("validation-utilities.lastName.required"),
  },
  {
    min: 2,
    message: i18n("validation-utilities.lastName.min"),
  },
];

export const emailRuleList = [
  {
    required: true,
    message: i18n("validation-utilities.email.required"),
  },
  {
    type: "email",
    message: i18n("validation-utilities.email.type"),
  },
  {
    min: 5,
    message: i18n("validation-utilities.email.min"),
  },
];

export const phoneNumberRuleList = [
  {
    min: 4,
    message: i18n("validation-utilities.phoneNumber.min"),
  },
];

export const localeRuleList = [
  {
    required: true,
    message: i18n("validation-utilities.locale.required"),
  },
];

export const roleRuleList = [
  {
    required: true,
    message: i18n("validation-utilities.role.required"),
  },
];

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
  if (!password) {
    return Promise.reject(i18n("validation-utilities.password.required"));
  }

  if (password.length < 8) {
    return Promise.reject(i18n("validation-utilities.password.minimumLength"));
  }

  if (!new RegExp("^.*[A-Z].*$").test(password)) {
    return Promise.reject(i18n("validation-utilities.password.uppercase"));
  }

  if (!new RegExp("^.*[a-z].*$").test(password)) {
    return Promise.reject(i18n("validation-utilities.password.lowercase"));
  }

  if (!new RegExp("^.*[0-9].*$").test(password)) {
    return Promise.reject(i18n("validation-utilities.password.number"));
  }

  if (!new RegExp("^.*[!@#$%^&*()+?/<>=.\\\\{}].*$").test(password)) {
    return Promise.reject(
      i18n("validation-utilities.password.specialCharacters")
    );
  }

  return Promise.resolve();
};
