import $ from "jquery";
import moment from "moment";

// get a readable string of the time from a given number of seconds
function getTimeFrom(seconds) {
  if (!isNaN(seconds)) {
    const now = moment();
    now.add(parseInt(seconds), "s");
    return now.fromNow(true);
  }
}

// Translate each token validity to a readable string
$(".tokenValidityInSeconds").each(function() {
  const $option = $(this);
  const text = getTimeFrom($option.text());
  $option.html(text);
});
