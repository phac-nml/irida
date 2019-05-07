/**
 * This file is loaded when the project sample linker command modal is loaded.
 */
import $ from "jquery";
import Clipboard from "clipboard";

const $btn = $(".js-clipboard-btn");

/*
Set up a new copy to clipboard
 */
const clipboard = new Clipboard(".js-clipboard-btn");
clipboard.on("success", function() {
  /*
  Add "has-success" highlight the input as green and displays
  success message.
   */
  $(".js-linker-form").addClass("has-success");
});
clipboard.on("error", function() {
  /*
  Add "has-error" highlight the input as red and displays
  error message.
   */
  $(".js-linker-form").addClass("has-error");
});
/*
Activate all tooltips.
 */
$btn.tooltip();
