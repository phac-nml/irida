(function($, tl) {
  $(function() {
    getEvents($("#size").val());

    $("#size").change(function() {
      getEvents($(this).val());
    });
  });

  function getEvents(size) {
    var eventUrl = tl.BASE_URL + 'events/current_user';
    $.ajax({
      method: 'GET',
      url: eventUrl,
      data: {
        size: size
      },
      headers: {
        Accept: 'text/html'
      },
      success: function(data, textStatus, jqXHR) {
        $("#events").html(data);
      }
    });
  }

})(window.jQuery, window.TL);