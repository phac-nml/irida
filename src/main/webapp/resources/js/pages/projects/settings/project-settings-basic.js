import $ from "jquery";
import "../../../../css/pages/project-settings-basic.css";

const projectSettings = (function (page, notifications) {
  $("#coverage-save").on("click", function () {
    const genomeSize = $("#genome-size").val();
    const minimumCoverage = $("#minimum-coverage").val();
    const maximumCoverage = $("#maximum-coverage").val();

    $.ajax({
      url: page.urls.coverage,
      type: "POST",
      data: {
        genomeSize: genomeSize,
        minimumCoverage: minimumCoverage,
        maximumCoverage: maximumCoverage,
      },
      statusCode: {
        200: function (response) {
          notifications.show({ text: response.result });

          if (minimumCoverage) {
            $("#minimum-coverage-display").html(minimumCoverage + "x");
          } else {
            $("#minimum-coverage-display").html(
              i18n("project.settings.notset")
            );
          }

          if (maximumCoverage) {
            $("#maximum-coverage-display").html(maximumCoverage + "x");
          } else {
            $("#maximum-coverage-display").html(
              i18n("project.settings.notset")
            );
          }

          if (genomeSize) {
            $("#genome-size-display").html(genomeSize + "bp");
          } else {
            $("#genome-size-display").html(i18n("project.settings.notset"));
          }

          $(".edit-coverage").toggle();
        },
      },
      fail: function () {
        notifications.show({
          text: i18n("project.settings.notifications.error"),
          type: "error",
        });
      },
    });
  });

  $("#edit-coverage-btn, #coverage-cancel").on("click", function () {
    $(".edit-coverage").toggle();
  });

  $("#confirm-deletion").on("change", function () {
    toggleDeleteButton();
  });

  /**
   * Updating analysis priority on server
   */
  $("#analysis-priority").on("change", function () {
    const priority = $("#analysis-priority").val();

    $.ajax({
      url: page.urls.priority,
      type: "POST",
      data: {
        priority: priority,
      },
      statusCode: {
        200: function (response) {
          notifications.show({ text: response.result });
        },
      },
      fail: function () {
        notifications.show({ text: page.i18n.error, type: "error" });
      },
    });
  });

  function toggleDeleteButton() {
    if ($("#confirm-deletion").is(":checked")) {
      $("#submit-delete").prop("disabled", false);
    } else {
      $("#submit-delete").prop("disabled", true);
    }
  }

  /**
   * Open a confirmation modal for removing an automated analysis pipeline
   * @param {number} templateId - the id for the analysis to remove
   */
  const displayRemoveAnalysisModal = (templateId) => {
    $("#removeAnalysisTemplateModal").load(
      `${window.PAGE.urls.deleteModal}#removeAnalysisTemplateModalGen`,
      { templateId },
      function () {
        $(this).modal("show");
      }
    );
  };

  /*
  Submission handler for when the user clicks on the remove analysis button.
   */
  $(".remove-analysis-form").on("submit", function (e) {
    e.preventDefault();
    const templateId = $(this).find(`input[name="templateId"]`).val();
    displayRemoveAnalysisModal(templateId);
  });
})(window.PAGE, window.notifications);
