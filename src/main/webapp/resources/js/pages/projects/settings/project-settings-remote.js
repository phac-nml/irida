import { showNotification } from "../../../modules/notifications";
import { updateRemoteConnectionStatus } from "../../remote-apis/remote-apis";

const API_ID = $("#connect-button").data("api-id");

$(".sync-setting").change(function() {
  var freq = $(this).val();

  updateSyncSettings({ frequency: freq });
});

$("#forceSync").on("click", function() {
  updateSyncSettings({ forceSync: "true" });
});

$("#becomeSyncUser").on("click", function() {
  updateSyncSettings({ changeUser: "true" });
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

function updateSyncSettings(data) {
  $.ajax({
    url: window.PAGE.urls.sync,
    type: "POST",
    data,
    statusCode: {
      200(response) {
        if (response.result) {
          showNotification({ text: response.result });
        } else if (response.error) {
          showNotification({ text: response.error, type: "error" });
        }
      }
    },
    error: function() {
      showNotification({ text: page.i18n.error, type: "error" });
    }
  });
}

// Initialize connection status
const $connectionWrapper = $(".connection-wrapper");
updateRemoteConnectionStatus($connectionWrapper, API_ID).then(response => {
  if (!response.includes("invalid_token")) {
    $(".api-connected-action").show();
  }
});
