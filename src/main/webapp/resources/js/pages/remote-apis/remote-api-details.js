/**
 * @file Used on the remote api details page to connect to a remote API and
 * update the DOM.
 */

import {
  initConnectRemoteApi,
  updateRemoteConnectionStatus
} from "./remote-apis";

const apiId = document.querySelector("#remoteapi-id").innerHTML;
const $connectionPanel = $(".connection-status-panel");

/**
 * Determine what the current connection status of the API is.
 */
updateRemoteConnectionStatus($connectionPanel, apiId);

/**
 * Initialize the remote connection button.
 */
initConnectRemoteApi(function() {
  updateRemoteConnectionStatus($connectionPanel, apiId);
});
