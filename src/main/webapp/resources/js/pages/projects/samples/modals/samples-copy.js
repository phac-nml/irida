/**
 * This script if for copying and moving samples from projects.
 * Loaded when the modal for either copying or moving samples is displayed.
 */
import $ from "jquery";
import "../../../../vendor/plugins/jquery/select2";
import { SAMPLE_EVENTS } from "../constants";
import { showNotification } from "../../../../modules/notifications";

/*
Set up the projects Select2 input
 */
const $input = $("#js-projectsSelect");
const url = $input.data("url");
const $submitBtn = $("#js-confirm");

$input.select2({
  theme: "bootstrap",
  minimumInputLength: 1,
  ajax: {
    url,
    dataType: "json",
    delay: 250,
    data(params) {
      return {
        term: params,
        page: params.page || 0,
        pageSize: 10
      };
    },
    results(data) {
      return {
        results: data.projects
      };
    },
    cache: true
  }
});

// Ensure select2 opens when tabbed into
$input
  .next(".select2")
  .find(".select2-selection")
  .focus(() => $input.select2("open"));

$input.on("select2-selecting", function(e) {
  $submitBtn.prop("disabled", false);
});

/*
Handle completing the copy
 */
$("#js-copy-form").submit(function(e) {
  e.preventDefault();
  const copyUrl = $submitBtn.data("url");
  $.post(copyUrl, $(this).serialize(), function(response) {
    /*
    Close the modal
     */
    $("#js-modal-wrapper").trigger(SAMPLE_EVENTS.SAMPLE_TOOLS_CLOSED);

    /*
    Alert the user that this was a success!
     */
    if (response.result === "success") {
      showNotification({
        type: response.result,
        text: response.message
      });
    } else {
      response.warnings.forEach(warning => {
        showNotification({
          type: "error",
          text: warning
        });
      });
    }
  });
});
