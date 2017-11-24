$(document).ready(function() {
  // default min password length
  const MIN_LENGTH = 6;
  // password form
  const $pwdForm = $(".password");
  // password text inputs
  const $pwdInputs = $("input[type=password]");
  // submit button element (should only be one!)
  const $submit = $("button[type=submit]");
  // password requirements container
  const $pwdReqs = $("#passwordRequirements");
  // get each element for each password requirement
  const $minLength = $("#password-minlength");
  const $uppercase = $("#password-uppercase");
  const $lowercase = $("#password-lowercase");
  const $number = $("#password-number");
  const $matching = $("#passwords-match");
  // array of arrays of jQuery element to restriction pattern
  const elRegex = [
    [$lowercase, new RegExp("^.*[a-z].*$")],
    [$uppercase, new RegExp("^.*[A-Z].*$")],
    [$number, new RegExp("^.*[0-9].*$")]
  ];
  const editPageRegex = new RegExp("^.*/users/\\d+/edit$");

  function checkIsEditPage(){
    return editPageRegex.test(document.URL);
  }

  function toggleIcon($el, isValid) {
    let errorClasses = "glyphicon-remove text-danger";
    let successClasses = "glyphicon-ok text-success";
    if (isValid) {
      $el.removeClass(errorClasses).addClass(successClasses);
    } else {
      $el.removeClass(successClasses).addClass(errorClasses);
    }
  }

  function toggleAlert($el, isValid) {
    let errorClasses = "alert-danger text-danger";
    let successClasses = "alert-success text-success";
    if (isValid) {
      $el.removeClass(errorClasses).addClass(successClasses);
    } else {
      $el.removeClass(successClasses).addClass(errorClasses);
    }
  }

  function toggleEnableSubmit($el, enable) {
    if (enable) {
      $el.removeClass("disabled").removeAttr("disabled");
    } else {
      $el.addClass("disabled").attr("disabled", "disabled");
    }
  }

  function checkPasswords() {
    // if success is true then enable submit button otherwise disable submit button
    let success = true;
    // get password and password confirmation values
    const pwd = $("#password").val();
    const pwdConfirmation = $("#confirmPassword").val();
    // Password length check
    const validLength = pwd.length >= MIN_LENGTH;
    success = success && validLength;
    toggleAlert($minLength, validLength);
    toggleIcon($minLength.find("i"), validLength);
    // check password against regex patterns
    for (let [$el, pattern] of elRegex) {
      const b = pattern.test(pwd);
      success = success && b;
      toggleAlert($el, b);
      toggleIcon($el.find("i"), b);
    }
    // check that passwords are matching
    const isMatch = (pwd === pwdConfirmation) && validLength;
    success = success && isMatch;
    toggleAlert($matching, isMatch);
    toggleIcon($matching.find("i"), isMatch);
    // is the current page the edit user info page?
    const isEditPage = checkIsEditPage();
    // is the current page the edit user info page and there are no passwords entered?
    // if so hide the password requirements since the user isn't changing the password
    const isEditPageNoPwds = (isEditPage && pwd === "" && pwdConfirmation === "");
    if (isEditPageNoPwds) {
      $pwdReqs.hide();
    } else {
      $pwdReqs.show();
    }
    // if all checks passed enable submit button, otherwise disable it
    toggleEnableSubmit($submit, (success || isEditPageNoPwds));
  };

  // Create User page "Require User Activation" checkbox (input#setpassword)
  // if unchecked then don't activate user, allow password to be set in page
  // if checked, don't ask for password for new user creation
  const $setPassword = $("#setpassword");

  // Password reset/Edit User pages
  // "Require User Activation" checkbox (input#setpassword) should not exist on password reset/edit user pages
  if ($setPassword.length === 0) {
    // setup password text input event handlers to check passwords
    $pwdInputs.keyup(checkPasswords);
    $pwdInputs.blur(checkPasswords);
    // initial check of passwords
    checkPasswords();
    return;
  }
  // Create User page
  if ($setPassword.prop("disabled")) {
    // if the activation e-mail checkbox is disabled, then we're not
    // configured to send activation e-mails to users. Uncheck the checkbox
    // and show the password entry fields.
    $setPassword.prop("checked", false);
    $pwdForm.attr("required", "required").show(100);
  } else {
    // if the activation e-mail checkbox is not disabled, then we're
    // *probably* configured to send e-mails (but maybe not correctly)
    // so add a change listener to show and hide the password entry
    // fields based on whether or not the checkbox is checked.
    $setPassword.change(function() {
      if ($(this).prop("checked")) {
        $pwdForm.hide(100);
        $pwdForm.removeAttr("required");
        $pwdInputs.off("blur", checkPasswords);
        $pwdInputs.off("keyup", checkPasswords);
        toggleEnableSubmit($submit, true);
      } else {
        $pwdForm.show(100);
        $pwdForm.attr("required", "required");
        $pwdInputs.keyup(checkPasswords);
        $pwdInputs.blur(checkPasswords);
        checkPasswords();
      }
    });
    // if "Require User Activation"
    if ($setPassword.prop("checked")) {
      $(".password").hide();
      // remove blur/keyup checkPasswords event handlers
      $pwdInputs.off("blur", checkPasswords);
      $pwdInputs.off("keyup",checkPasswords);
      toggleEnableSubmit($submit, true);
    } else {
      // add blur/keyup checkPasswords event handlers
      $pwdInputs.keyup(checkPasswords);
      $pwdInputs.blur(checkPasswords);
      // force check passwords
      checkPasswords();
    }
  }
});
