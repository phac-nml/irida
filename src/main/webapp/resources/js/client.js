import $ from "jquery";
import { getDurationFromSeconds } from "./utilities/date-utilities";

function handleChecked(item, itemToShow) {
  if (item.checked) {
    $(itemToShow).show();
  } else {
    $(itemToShow).hide();
  }
}

// Translate each token validity to a readable string
$(".tokenValidityInSeconds").each(function() {
  const $option = $(this);
  const text = getDurationFromSeconds($option.text());
  $option.html(text);
});

$("#scope_read").change(function() {
  handleChecked(this, "#scope_auto_read_div");
});

$("#refresh").change(function() {
  handleChecked(this, "#refresh-token-container");
});

$("#scope_write").change(function() {
  handleChecked(this, "#scope_auto_write_div");
});

$("#authorizedGrantTypes").on("change", function() {
  const value = $(this)
    .children("option:selected")
    .val();
  if (value === "authorization_code") {
    $("#redirect-container").show();
  } else {
    $("#redirect-container").hide();
  }
});
