import $ from "jquery";
import "jquery-validation";
import "./../../vendor/plugins/jquery/select2";

const form = $("#create-sample-form");
const saveBtn = $("#save-btn");

// SAMPLE NAME VALIDATION
/**
 * Sample Name Validation.  Must only be:
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
$.validator.addMethod("checkallowedchars", value => {
  return /^[A-Za-z\d-_!@#$%~`]+$/i.test(value);
});

form.validate({
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
  rules: {
    sampleName: {
      required: true,
      minlength: 3,
      checkallowedchars: true,
      // Server validation to ensure that the label is not already used
      // within this project.
      remote: {
        url: window.PAGE.urls.validateName
      }
    }
  },
  // Disable the button of clicking to prevent multiple clicks.
  submitHandler(form) {
    saveBtn.attr("disabled", true);
    form.submit();
  }
});

// Update the form submit button on changes to the sample name
form.find("#sampleName").on("keyup blur", () => {
  // Give the server time to decide if this is a valid name.
  setTimeout(() => {
    saveBtn.prop("disabled", form.valid() ? false : "disabled");
  }, 300);
});

// Set up the organism field
const organismInput = $("#organism");

organismInput.select2({
  minimumInputLength: 1,
  ajax: {
    url: window.PAGE.urls.taxonomy,
    dataType: "json",
    delay: 250,
    data(params) {
      return {
        searchTerm: params.term
      };
    },
    processResults(data) {
      return {
        results: data
      };
    },
    cache: true
  }
});

// Ensure select2 opens when tabbed into
organismInput
  .next(".select2")
  .find(".select2-selection")
  .focus(() => organismInput.select2("open"));

// When an organism is selected move to the submit button
organismInput.on("change", () => {
  // Event happens to fast.  Need to give a little time for
  // the browser to catch up.  Then focus on the button.
  setTimeout(() => {
    saveBtn.focus();
  }, 100);
});
