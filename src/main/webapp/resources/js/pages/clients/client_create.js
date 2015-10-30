(function ($) {
  $(function() {
    $("#tokenValidity").select2().select2("val", [[${given_tokenValidity}]]);
    $("#authorizedGrantTypes").select2();
    $("#scope_read").change(function() {
      handleChecked(this,"#scope_auto_read_div");
    });
    $("#scope_write").change(function() {
      handleChecked(this,"#scope_auto_write_div");
    });

    var handleChecked = function(item, itemToShow) {
      if(item.checked) {
        $(itemToShow).show();
      }
      else {
        $(itemToShow).hide();
      }
    }
  });
})(window.jQuery);