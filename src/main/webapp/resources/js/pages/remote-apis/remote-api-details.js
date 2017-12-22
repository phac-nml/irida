import {
  initConnectRemoteApi,
  updateRemoteConnectionStatus
} from "./remote-apis";

const apiId = document.querySelector("#remoteapi-id").innerHTML;
const $connectionPanel = $(".connection-status-panel");

updateRemoteConnectionStatus($connectionPanel, apiId);

initConnectRemoteApi(function() {
  updateRemoteConnectionStatus($connectionPanel, apiId);
});
