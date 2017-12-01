import $ from "jquery";

// Configuration required by jquery-validate to use
// bootstrap style errors.
export const validationConfig = {
  errorElement: "em",
  errorPlacement: function(error, element) {
    error.addClass("help-block");
    error.insertAfter(element);
  },
  highlight(element) {
    const fg = $(element).parents(".form-group");
    if (!fg.hasClass("has-error")) {
      fg.addClass("has-error").removeClass("has-success");
    }
  },
  unhighlight(element) {
    const fg = $(element).parents(".form-group");
    if (fg.hasClass("has-error")) {
      fg.removeClass("has-error")
        .addClass("has-success");
      setTimeout(() => fg.removeClass("has-success"), 1000);
    }
  },
  // Disable the button of clicking to prevent multiple clicks.
  submitHandler(form) {
    $(form).find(":submit").attr("disabled", true);
    form.submit();
  }
};

/**
 * When called it adds a validation to jquery-validate that
 * checks the name only includes:
 * 1. Letter (upper or lowercase)
 * 2. Number
 * 3. _ (underscore)
 * 4. - (hyphen)
 * 5. !
 * 6. @
 * 7. #
 * 8. $
 * 9. %
 * 10. ~
 * 11. ` (back tick)
 */
export function sampleNameCharacterValidation() {
  if (typeof $.validator === "function") {
    $.validator.addMethod("checkallowedchars", value => {
      return /^[A-Za-z\d-_!@#$%~`]+$/i.test(value);
    });
    return;
  }
  throw new Error(
    "jquery-validate must be loaded to activate sample name checker"
  );
}


export function passwordCharacterReqsValidation() {
  if (typeof $.validator === "function") {
    $.validator.addMethod("hasLowercaseLetter", value => {
      return /^.*[a-z].*$/.test(value);
    });
    $.validator.addMethod("hasUppercaseLetter", value => {
      return /^.*[A-Z].*$/.test(value);
    });
    $.validator.addMethod("hasNumber", value => {
      return /^.*[0-9].*$/.test(value);
    });
    $.validator.addMethod("hasSpecialChar", value => {
      return /^.*[!@#$%^&*()+?/<>=.\\{}].*$/.test(value);
    });
    return;
  }
  throw new Error(
    "jquery-validate must be loaded to activate password checker"
  );
}
