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

function updateConnection(apiId) {
  updateRemoteConnectionStatus($connectionWrapper, apiId).then(response => {
    if (!response.includes("invalid_token")) {
      getApiProjects(apiId);
    }
  });
}

$apiSelection.on("change", function() {
  $connectBtn.addClass("hidden");
  const apiId = $(this).val();
  document.querySelector("#connect-button").dataset.apiId = apiId;
  updateConnection(apiId);
});

$projectSelect.on("change", function() {
  let projectUrl = $(this).val();
  if (projectUrl === 0) {
    projectUrl = null;
  }
  $("#projectUrl").val(projectUrl);
});

function getApiProjects(apiId) {
  const url = `${PAGE.urls.apiProjectList}${apiId}`;

  $(".project-option").remove();
  $("#projectUrl").val("");

  $.ajax({
    url: url,
    success: function(vals) {
      $(".project-option").remove();

      $.each(vals, function(i, response) {
        var project = response.project;
        var status = response.remoteStatus;
        var projectUrl = status.url;
        $projectSelect.append(
          `<option class="project-option" value="${projectUrl}">${
            project.label
          }</option>`
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
