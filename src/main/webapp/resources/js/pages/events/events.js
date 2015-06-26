(function($, tl) {
  $(function() {
    getEvents($('#size').val());

    $('#size').change(function() {
      getEvents($(this).val());
    });
  });

  function getEvents(size) {
    var eventUrl = $('#eventURL').val();
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
        $('#events').html(data);
      }
    });
  }

})(window.jQuery, window.TL);