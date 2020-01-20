/**
 * @file Responsible for setting up new project synchronizations with a remote API.
 * Loaded onto pages/projects/project_sync.html
 */

import $ from "jquery";
import {
  initConnectRemoteApi,
  updateRemoteConnectionStatus
} from "../remote-apis/remote-apis";

const $connectBtn = $("#connect-button");
const $connectionWrapper = $(".connection-wrapper");
const $apiSelection = $("#api-selection");
const originalApiId = $apiSelection.val();
// jQuery .data("api-id", id) does not write back to the DOM element for
// vanilla js to query.
document.querySelector("#connect-button").dataset.apiId = originalApiId;
const $projectSelect = $("#project-select");

/**
 * Update the status of the connection to the remote api.  Used both to initial
 * the status of the connection and then to update once a connection has been made.
 * @param {number} apiId identifier the for the api to update.
 */
function updateConnection(apiId) {
  updateRemoteConnectionStatus($connectionWrapper, apiId).then(response => {
    if (!response.includes("invalid_token")) {
      getApiProjects(apiId);
    }
  });
}

/**
 * Listen for changes in the api selection, and update the connection status / connect
 * button.
 */
$apiSelection.on("change", function() {
  $connectBtn.addClass("hidden");
  const apiId = $(this).val();
  document.querySelector("#connect-button").dataset.apiId = apiId;
  updateConnection(apiId);
});

/**
 * Listen for changes in the project selection based on the currently selected Remote API
 * and update the selected project value.
 */
$projectSelect.on("change", function() {
  let projectUrl = $(this).val();
  if (projectUrl === 0) {
    projectUrl = null;
  }
  $("#projectUrl").val(projectUrl);
});

/**
 * Get a list of project from the server for the currently selected remote api.
 * @param {number} apiId selected API identifier.
 */
function getApiProjects(apiId) {
  const url = `${PAGE.urls.apiProjectList}${apiId}`;

  // Remove any current projects, they would be from a different API.
  $(".project-option").remove();
  $("#projectUrl").val("");

  $.ajax({
    url,
    success(vals) {
      $(".project-option").remove();

      $.each(vals, function(i, response) {
        const project = response.project;
        const status = response.remoteStatus;
        const projectUrl = status.url;
        $projectSelect.append(
          `<option class="project-option" value="${projectUrl}">${project.label}</option>`
        );
      });
      $projectSelect.prop("disabled", false);
    },
    error: function() {
      $projectSelect.prop("disabled", true);
    }
  });
}

/*
Initialize the page.
 */
initConnectRemoteApi(updateConnection);
updateConnection(originalApiId);
