(function($, moment) {
  // get a readable string of the time from a given number of seconds
  function getTimeFrom(seconds) {
    if (!isNaN(seconds)) {
      var now = moment();
      now.add(parseInt(seconds), "s");
      return now.fromNow(true);
    }
  }

  // Translate each token validity to a readable string
  $(".tokenValidityInSeconds").each(function() {
    var $option = $(this);
    var text = getTimeFrom($option.text());
    $option.html(text);
  });
})(window.jQuery, window.moment);
