import $ from "jquery";
import "jquery-validation";

import {passwordCharacterReqsValidation, validationConfig}
  from "../../utilities/form-validation";

const PWD_MIN_LENGTH = 8;

// Create User page "Require User Activation" checkbox (input#setpassword)
// if unchecked then don't activate user, allow password to be set in page
// if checked, don't ask for password for new user creation
const $setPassword = $("#setpassword");

const $pwdFormGroup = $(".password");
const $pwdForm = $pwdFormGroup.parents("form");
const $submit = $(":submit");
const $pwdInputs = $(":password");

//const editPageRegex = new RegExp("^.*/users/\\d+/edit$");
const isEditPage = /^.*\/users\/\d+\/edit$/.test(document.URL);

// Set up password requirements validation methods
passwordCharacterReqsValidation();

function submitBtnToggle() {
  return $submit.prop("disabled", $pwdForm.valid() ? false : "disabled");
}

function passwordResetValidation() {
  $pwdForm.validate(Object.assign({}, validationConfig, 
    {
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
          equalTo: "#password"
        }
      }
    }
  ));
  $pwdInputs.on("keyup blur", submitBtnToggle);
}

function editUserDetailsValidation() {
  $pwdForm.validate(Object.assign({}, validationConfig, 
  {
    rules: {
      firstName: {
        minlength: 2,
      },
      lastName: {
        minlength: 2,
      },
      email: {
        minlength: 5,
      },
      password: {
        minlength: PWD_MIN_LENGTH,
        hasUppercaseLetter: true,
        hasLowercaseLetter: true,
        hasNumber: true,
        hasSpecialChar: true,
      },
      confirmPassword: {
        equalTo: "#password"
      }
    },
    // don't validate password input or other non-password inputs if empty/blank
    ignore: "#password:blank,input:blank:not(:password)"
  }));
  $pwdInputs.on("keyup blur", submitBtnToggle);
}

function createUserValidation() {
  // TODO: add remote checks for existing username and email https://jqueryvalidation.org/remote-method/
  const createUserValidation = Object.assign({}, validationConfig, 
    {
      rules: {
        username: {
          required: true,
          minlength: 3,
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
          equalTo: "#password"
        },
      },
      ignore: ":hidden,:disabled"
    }
  );

  function toggleShowPassword() {
    if ($setPassword.prop("checked")) {
      $pwdFormGroup.hide();
      $pwdInputs.removeAttr("required");
    } else {
      $pwdFormGroup.show();
      $pwdInputs.attr("required", "required");
    }
  }

  $pwdForm.validate(createUserValidation);
  // if "Require User Activation" then hide password inputs, otherwise require passwords
  toggleShowPassword();
  if ($setPassword.prop("disabled")) {
    // if the activation e-mail checkbox is disabled, then we're not
    // configured to send activation e-mails to users. Uncheck the checkbox
    // and show the password entry fields.
    $setPassword.prop("checked", false);
    $pwdFormGroup.show();
    $pwdInputs.attr("required", "required");
  } else {
    // if the activation e-mail checkbox is not disabled, then we're
    // *probably* configured to send e-mails (but maybe not correctly)
    // so add a change listener to show and hide the password entry
    // fields based on whether or not the checkbox is checked.
    $setPassword.change(toggleShowPassword);
  }
}

// Setup form validation based on page
// No "Require User Activation" checkbox (input#setpassword) should exist 
// on password reset/edit user pages
if ($setPassword.length === 0 && !isEditPage) {
  passwordResetValidation();
} else if ($setPassword.length === 0 && isEditPage) {
  editUserDetailsValidation();
} else {
  createUserValidation();
}
