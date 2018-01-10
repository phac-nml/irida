import { showNotification } from "../../../modules/notifications";
import {
  initConnectRemoteApi,
  updateRemoteConnectionStatus
} from "../../remote-apis/remote-apis";

const $connectionWrapper = $(".connection-wrapper");
const API_ID = $("#connect-button").data("api-id");

/**
 * Check the current connection status with the server.
 */
function updateConnection(api) {
  updateRemoteConnectionStatus($connectionWrapper, api).then(response => {
    if (!response.includes("invalid_token")) {
      $(".api-connected-action").show();
    }
  });
}

$(".sync-setting").change(function() {
  const frequency = $(this).val();
  updateSyncSettings({ frequency });
});

$("#forceSync").on("click", function() {
  updateSyncSettings({ forceSync: "true" });
});

$("#becomeSyncUser").on("click", function() {
  updateSyncSettings({ changeUser: "true" });
});

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

/*
Initialize the page.
 */
initConnectRemoteApi(updateConnection);
updateConnection(API_ID);
