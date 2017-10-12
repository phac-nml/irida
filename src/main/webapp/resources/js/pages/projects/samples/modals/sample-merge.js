import $ from "jquery";
import "jquery-validation";
import {
  validationConfig,
  minLengthIfPresentValidation,
  sampleNameCharacterValidation
} from "../../../../utilities/form-validation";

/*
Set up sample name validation
 */
sampleNameCharacterValidation();
minLengthIfPresentValidation();

const config = Object.assign({}, validationConfig, {
  debug: true,
  onkeyup(element) {
    $(element).valid();
  },
  rules: {
    sampleName: {
      minLengthIfPresent: 3,
      checkallowedchars: true,
      // Server validation to ensure that the label is not already used
      // within this project.
      remote(element) {
        return {
          url: $(element).data("validateUrl")
        };
      }
    }
  },
  submitHandler(form) {
    const $form = $(form);
    const url = $form.prop("action");
    $.post(url, $form.serialize(), function(response) {
      /*
      Close the modal
       */
      $("#modal-wrapper").trigger("samples:merged");

      /*
      Alert the user that this was a success!
       */
      window.notifications.show({
        type: response.result,
        msg: response.message
      });
    });
  }
});

$("#mergeForm").validate(config);
