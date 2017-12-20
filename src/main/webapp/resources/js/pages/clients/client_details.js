(function($, noty, page) {
  $(function() {
    $("#remove-btn").click(function() {
      noty({
        layout: "center",
        type: "default",
        text: page.removeNoty.text,
        modal: true,
        buttons: [
          {
            addClass: "btn btn-primary btn-sm confirm-delete",
            text: page.buttons.ok,
            onClick: function() {
              $("#removeForm").submit();
            }
          },
          {
            addClass: "btn btn-default btn-sm",
            text: page.buttons.cancel,
            onClick: function($noty) {
              $noty.close();
            }
          }
        ]
      });
    });

    $("#revoke-btn").click(function() {
      noty({
        layout: "center",
        type: "default",
        text: page.revokeNoty.text,
        modal: true,
        buttons: [
          {
            addClass: "btn btn-primary btn-sm confirm-revoke",
            text: page.buttons.ok,
            onClick: function() {
              $("#revokeForm").submit();
            }
          },
          {
            addClass: "btn btn-default btn-sm",
            text: page.buttons.cancel,
            onClick: function($noty) {
              $noty.close();
            }
          }
        ]
      });
    });
  });
})(window.jQuery, window.noty, window.PAGE);
