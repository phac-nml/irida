(function($, page) {
  $(function() {
    getEvents($('#size').val());

    $('#size').change(function() {
      getEvents($(this).val());
    });
  });

  function getEvents(size) {
    $.ajax({
      method: 'GET',
      url: page.urls.events,
      data: {
        size: size
      },
      headers: {
        Accept: 'text/html'
      },
      success: function(data, textStatus, jqXHR) {
        $('#events').html(data);
      }
    });
  }

})(window.jQuery, window.PAGE);
