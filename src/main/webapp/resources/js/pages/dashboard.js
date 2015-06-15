(function ($, tl) {
  $(function () {
    $.ajax({
      method : 'GET',
      url    : tl.BASE_URL + 'events/current_user/',
      headers: {
        Accept: 'text/html'
      },
      success: function (data) {
        $('#events').html(data);
      }
    });
  });
})(window.jQuery, window.TL);