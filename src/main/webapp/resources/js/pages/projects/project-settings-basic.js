const projectSettings = (function(page, notifications) {
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

  /**
   * Button for removing an analysis template from a project.  This should show a confirmation modal
   */
  $(".analysis-remove").on("click", function() {
    const templateId = $(this)
      .closest("li")
      .data("analysis");

    /*
    Display the confirmation modal for removing a template from the project.
     */
    $("#removeAnalysisTemplateModal").load(
      `${window.PAGE.urls.deleteModal}#removeAnalysisTemplateModalGen`,
      { templateId },
      function() {
        const modal = $(this);
        modal.modal("show");
      }
    );
  });

  function toggleDeleteButton() {
    if ($("#confirm-deletion").is(":checked")) {
      $("#submit-delete").prop("disabled", false);
    } else {
      $("#submit-delete").prop("disabled", true);
    }
  }
})(window.PAGE, window.notifications);
