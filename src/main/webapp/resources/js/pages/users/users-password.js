import $ from "jquery";
import "jquery-validation";

import {
  passwordCharacterReqsValidation,
  validationConfig,
} from "../../utilities/form-validation";
import { setBaseUrl } from "../../utilities/url-utilities";

// Minimum password length
const PWD_MIN_LENGTH = 8;

// Create User page "Require User Activation" checkbox (input#setpassword)
// if unchecked then don't activate user, allow password to be set in page
// if checked, don't ask for password for new user creation
const $setPasswordCb = $(".js-set-password-cb");
const $pwdWrapper = $(".js-password-wrapper");
const $pwdForm = $pwdWrapper.parents("form");
const $submit = $(".js-submit-btn");
const $pwdInputs = $(".js-password-input");

// Set up password requirements validation methods
passwordCharacterReqsValidation();

function submitBtnToggle() {
  return $submit.prop("disabled", $pwdForm.valid() ? false : "disabled");
}

function getUsernameValidationUrl() {
  return setBaseUrl("users/validate-username");
}

function getEmailValidationUrl() {
  return setBaseUrl("users/validate-email");
}

function passwordResetValidation() {
  $pwdForm.validate(
    Object.assign({}, validationConfig, {
      rules: {
        password: {
          required: true,
          minlength: PWD_MIN_LENGTH,
          hasUppercaseLetter: true,
          hasLowercaseLetter: true,
          hasNumber: true,
          hasSpecialChar: true,
        },
        confirmPassword: {
          required: true,
          equalTo: "#password",
        },
      },
    })
  );
  $pwdInputs.on("keyup blur", submitBtnToggle);
}

function editUserDetailsValidation() {
  $pwdForm.validate(
    Object.assign({}, validationConfig, {
      rules: {
        firstName: {
          minlength: 2,
        },
        lastName: {
          minlength: 2,
        },
        phoneNumber: {
          minlength: 4,
          hasNumber: true,
        },
        email: {
          minlength: 5,
          remote: getEmailValidationUrl(),
        },
        password: {
          minlength: PWD_MIN_LENGTH,
          hasUppercaseLetter: true,
          hasLowercaseLetter: true,
          hasNumber: true,
          hasSpecialChar: true,
        },
        confirmPassword: {
          equalTo: "#password",
        },
      },
      // don't validate password input or other non-password inputs if empty/blank
      ignore: "#password:blank,input:blank:not(.js-password-input)",
    })
  );
  $pwdInputs.on("keyup blur", submitBtnToggle);
}

function createUserValidation() {
  const createUserValidation = Object.assign({}, validationConfig, {
    rules: {
      username: {
        required: true,
        minlength: 3,
        remote: getUsernameValidationUrl(),
      },
      firstName: {
        required: true,
        minlength: 2,
      },
      lastName: {
        required: true,
        minlength: 2,
      },
      email: {
        required: true,
        minlength: 5,
        remote: getEmailValidationUrl(),
      },
      phoneNumber: {
        required: true,
        minlength: 4,
        hasNumber: true,
      },
      password: {
        required: true,
        minlength: PWD_MIN_LENGTH,
        hasUppercaseLetter: true,
        hasLowercaseLetter: true,
        hasNumber: true,
        hasSpecialChar: true,
      },
      confirmPassword: {
        required: true,
        equalTo: "#password",
      },
    },
    ignore: ":hidden,:disabled",
  });

  function toggleShowPassword() {
    if ($setPasswordCb.prop("checked")) {
      $pwdWrapper.hide();
      $pwdInputs.removeAttr("required");
    } else {
      $pwdWrapper.show();
      $pwdInputs.attr("required", "required");
    }
  }

  $pwdForm.validate(createUserValidation);
  // if "Require User Activation" then hide password inputs, otherwise require passwords
  toggleShowPassword();
  if ($setPasswordCb.prop("disabled")) {
    // if the activation e-mail checkbox is disabled, then we're not
    // configured to send activation e-mails to users. Uncheck the checkbox
    // and show the password entry fields.
    $setPasswordCb.prop("checked", false);
    $pwdWrapper.show();
    $pwdInputs.attr("required", "required");
  } else {
    // if the activation e-mail checkbox is not disabled, then we're
    // *probably* configured to send e-mails (but maybe not correctly)
    // so add a change listener to show and hide the password entry
    // fields based on whether or not the checkbox is checked.
    $setPasswordCb.change(toggleShowPassword);
  }
}

const isEditPage = /^.*\/users\/\d+\/edit$/.test(document.URL);
// Setup form validation based on page
// No "Require User Activation" checkbox (input#setpassword) should exist
// on password reset/edit user pages
if ($setPasswordCb.length === 0 && !isEditPage) {
  passwordResetValidation();
} else if ($setPasswordCb.length === 0 && isEditPage) {
  editUserDetailsValidation();
} else {
  createUserValidation();
}
