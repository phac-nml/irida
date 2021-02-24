import $ from "jquery";
import "../../../../css/pages/project-settings-basic.css";

(function (page, notifications) {
  const $confirmDeletionCB = $("#confirm-deletion");

  function toggleDeleteButton() {
    if ($confirmDeletionCB.is(":checked")) {
      $("#submit-delete").prop("disabled", false);
    } else {
      $("#submit-delete").prop("disabled", true);
    }
  }

  $confirmDeletionCB.on("change", function () {
    toggleDeleteButton();
  });
})(window.PAGE, window.notifications);
