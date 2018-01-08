/**
 * This file is for the Project > Project Samples Page.
 * Handles validation logic for a new sample name.
 */
import $ from "jquery";
import "jquery-validation";
import {
  minLengthIfPresentValidation,
  sampleNameCharacterValidation,
  validationConfig
} from "../../../../utilities/form-validation";
import { SAMPLE_EVENTS } from "../constants";
import { showNotification } from "../../../../modules/notifications";

/*
Set up sample name validation
 */
sampleNameCharacterValidation();
minLengthIfPresentValidation();

/**
 * Configuration object for jQuery Validate.
 */
const config = Object.assign({}, validationConfig, {
  // Validate the field each time the key is pressed, this will ensure that
  // the field updates properly.
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
      $("#js-modal-wrapper").trigger(SAMPLE_EVENTS.SAMPLE_TOOLS_CLOSED);

      /*
      Alert the user that this was a success!
       */
      showNotification({
        type: response.result,
        text: response.message
      });
    });
  }
});

$("#mergeForm").validate(config);
