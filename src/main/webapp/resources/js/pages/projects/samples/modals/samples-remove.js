/**
 * This script if for copying and moving samples from projects.
 * Loaded when the modal for either copying or moving samples is displayed.
 */
import $ from "jquery";

$("#js-remove-modal").submit(function(e) {
  e.preventDefault();

  $.post({
    url: $("#js-submit").data("url")
  });
});
