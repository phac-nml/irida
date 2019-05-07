(function($, page) {
  $(function() {
    $("#scope_read").change(function() {
      handleChecked(this, "#scope_auto_read_div");
    });
    $("#scope_write").change(function() {
      handleChecked(this, "#scope_auto_write_div");
    });

    $("#refresh").change(function() {
      handleChecked(this, "#refresh-token-container");
    });

    var handleChecked = function(item, itemToShow) {
      if (item.checked) {
        $(itemToShow).show();
      } else {
        $(itemToShow).hide();
      }
    };
  });
})(window.jQuery, window.PAGE);
