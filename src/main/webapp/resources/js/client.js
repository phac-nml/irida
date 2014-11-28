$(function () {
// get a readable string of the time from a given number of seconds
  function getTimeFrom(seconds) {
    var timeFromEpoch = (new Date().getTime() / 1000) + parseInt(seconds);
    return moment.unix(timeFromEpoch).fromNow(true);
  }

  // Translate each token validity to a readable string
  $(".tokenValidityInSeconds").each(function () {
    var $option = $(this);
    var text = getTimeFrom($option.text());
    $option.html(text);
  });
})();