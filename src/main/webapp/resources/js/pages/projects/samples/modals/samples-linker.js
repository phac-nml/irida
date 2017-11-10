import $ from "jquery";
import Clipboard from "clipboard";

const $btn = $(".js-clipboard-btn");

const clipboard = new Clipboard(".js-clipboard-btn");
clipboard.on("success", function(e) {
  $(".js-linker-form").addClass("has-success");
});
clipboard.on('error', function(e) {
  $(".js-linker-form").addClass("has-error");
});

$btn.tooltip();
