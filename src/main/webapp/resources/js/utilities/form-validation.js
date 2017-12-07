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
    $(element)
      .parents(".form-group")
      .addClass("has-error")
      .removeClass("has-success");
  },
  unhighlight(element) {
    $(element)
      .parents(".form-group")
      .addClass("has-success")
      .removeClass("has-error");
  },
  // Disable the button of clicking to prevent multiple clicks.
  submitHandler(form) {
    saveBtn.attr("disabled", true);
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
      return value.length === 0 || /^[A-Za-z\d-_!@#$%~`]+$/i.test(value);
    });
    return;
  }
  throw new Error(
    "jquery-validate must be loaded to activate sample name checker"
  );
}

/**
 * Add a validator that can be used within an optional input, that has a
 * minlength if present.
 */
export function minLengthIfPresentValidation() {
  if (typeof $.validator === "function") {
    $.validator.addMethod("minLengthIfPresent", (value, element, size) => {
      return value.length === 0 || value.length >= size;
    });
    return;
  }
  throw new Error(
    "jquery-validate must be loaded to activate sample name checker"
  );
}