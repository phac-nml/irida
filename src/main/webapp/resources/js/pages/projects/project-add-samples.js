import $ from "jquery";
import "jquery-validation";
import "../../vendor/plugins/jquery/select2";
import {
  sampleNameCharacterValidation,
  validationConfig
} from "../../utilities/form-validation";

const form = $("#create-sample-form");
const saveBtn = $("#save-btn");

// Set up sample name character validation.
sampleNameCharacterValidation();

// Activate jquery-validate on the form.
const formConfig = Object.assign({}, validationConfig, {
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
  }
});
form.validate(formConfig);

// Update the form submit button on changes to the sample name
form.find("#sampleName").on("keyup blur", () => {
  // Give the server time to decide if this is a valid name.
  setTimeout(() => {
    saveBtn.prop("disabled", form.valid() ? false : "disabled");
  }, 300);
});

// Set up the organism field select2 input
const organismInput = $("#organism");

organismInput.select2({
  theme: "bootstrap",
  minimumInputLength: 1,
  ajax: {
    url: window.PAGE.urls.taxonomy,
    dataType: "json",
    delay: 250,
    data(searchTerm) {
      return {
        searchTerm
      };
    },
    results(data) {
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
