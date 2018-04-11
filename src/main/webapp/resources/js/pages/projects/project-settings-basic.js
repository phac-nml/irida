const projectSettings = (function(page, notifications) {
  $("#assemble").change(function() {
    const checkbox = $(this);
    const assemble = checkbox.is(":checked");

    $.ajax({
      url: page.urls.assemble,
      type: "POST",
      data: {
        assemble: assemble
      },
      statusCode: {
        200: function(response) {
          notifications.show({ text: response.result });
        }
      },
      fail: function() {
        notifications.show({ text: page.i18n.error, type: "error" });
      }
    });
  });

  // Sistr settings
  const SISTR_TYPES = {
    off: "OFF",
    auto: "AUTO",
    autoMetadata: "AUTO_METADATA"
  };

  $(".js-sistr-checkbox").on("change", function(e) {
    const selected = $(e.target).prop("checked");
    if (selected) {
      $(".js-sistr-writes").removeAttr("disabled");
      updateSistrSettings(SISTR_TYPES.auto);
    } else {
      $(".js-sistr-writes")
        .removeAttr("checked")
        .attr("disabled", true);
      updateSistrSettings(SISTR_TYPES.off);
    }
  });

  $(".js-sistr-writes").on("change", function(e) {
    const selected = $(e.target).prop("checked");
    if (selected) {
      updateSistrSettings(SISTR_TYPES.autoMetadata);
    } else {
      updateSistrSettings(SISTR_TYPES.auto);
    }
  });

  function updateSistrSettings(sistr) {
    $(".js-sistr-checkbox").val(sistr);
    $.ajax({
      url: page.urls.sistr,
      type: "POST",
      data: {
        sistr
      },
      statusCode: {
        200: function(response) {
          notifications.show({ text: response.result });
        }
      },
      fail: function() {
        notifications.show({ text: page.i18n.error, type: "error" });
      }
    });
  }

  $("#coverage-save").on("click", function() {
    const genomeSize = $("#genome-size").val();
    const minimumCoverage = $("#minimum-coverage").val();
    const maximumCoverage = $("#maximum-coverage").val();

    $.ajax({
      url: page.urls.coverage,
      type: "POST",
      data: {
        genomeSize: genomeSize,
        minimumCoverage: minimumCoverage,
        maximumCoverage: maximumCoverage
      },
      statusCode: {
        200: function(response) {
          notifications.show({ text: response.result });

          if (minimumCoverage) {
            $("#minimum-coverage-display").html(minimumCoverage + "x");
          } else {
            $("#minimum-coverage-display").html(page.i18n.not_set);
          }

          if (maximumCoverage) {
            $("#maximum-coverage-display").html(maximumCoverage + "x");
          } else {
            $("#maximum-coverage-display").html(page.i18n.not_set);
          }

          if (genomeSize) {
            $("#genome-size-display").html(genomeSize + "bp");
          } else {
            $("#genome-size-display").html(page.i18n.not_set);
          }

          $(".edit-coverage").toggle();
        }
      },
      fail: function() {
        notifications.show({ text: page.i18n.error, type: "error" });
      }
    });
  });

  $("#edit-coverage-btn, #coverage-cancel").on("click", function() {
    $(".edit-coverage").toggle();
  });

  $("#confirm-deletion").on("change", function() {
    toggleDeleteButton();
  });

  function toggleDeleteButton() {
    if ($("#confirm-deletion").is(":checked")) {
      $("#submit-delete").prop("disabled", false);
    } else {
      $("#submit-delete").prop("disabled", true);
    }
  }
})(window.PAGE, window.notifications);
