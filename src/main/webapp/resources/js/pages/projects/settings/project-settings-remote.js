/**
 * @file responsible for handling interactions on the Project > Settings > Remote Page.
 */

import { showNotification } from "../../../modules/notifications";
import {
  initConnectRemoteApi,
  updateRemoteConnectionStatus
} from "../../remote-apis/remote-apis";

const $connectionWrapper = $(".connection-wrapper");
const API_ID = $("#connect-button").data("api-id");

/**
 * Check the current connection status with the server, and update the UI accordingly.
 */
function updateConnection(api) {
  updateRemoteConnectionStatus($connectionWrapper, api).then(response => {
    if (!response.includes("invalid_token")) {
      $(".api-connected-action").show();
    }
  });
}

/**
 * Update the frequency of the sync event.
 */
$(".sync-setting").change(function() {
  const frequency = $(this).val();
  updateSyncSettings({ frequency });
});

/**
 * Sync the project now, instead of waiting until the next scheduled sync.
 */
$("#forceSync").on("click", function() {
  updateSyncSettings({ forceSync: "true" });
});

/**
 * Set the current user as the sync user.
 */
$("#becomeSyncUser").on("click", function() {
  updateSyncSettings({ changeUser: "true" });
});

/**
 * Update sync setting for this remote project.
 * @param {object} data
 */
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
      showNotification({
        text: i18n("project.settings.notifications.error"),
        type: "error"
      });
    }
  });
}

/*
Initialize the page.
 */
initConnectRemoteApi(updateConnection);
updateConnection(API_ID);
