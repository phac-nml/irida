/**
 * @file helper functions used by all pages that require connection with a remote API.
 */

/**
 * Get the status of a Remote API. And get the DOM required to display it.
 * @param {number} apiId Identifier for the remote API to connect to.
 * @returns {node} DOM element containing the status of the API.
 */
function getApiStatus(apiId) {
  return $.ajax({
    url: `${window.TL.BASE_URL}remote_api/status/web/${apiId}`,
    type: "GET",
    dataType: "html"
  });
}

/**
 * Update the status of a remote API connection.  This updates the interface with the $container
 *
 * Expected DOM within the container:
 * <code>
 *   <div>
 *    <div class="js-status-wrapper">  </div>
 *    <div class="oauth-connect-link"></div>
 *   </div>
 * </code>
 * @param $container
 * @param apiId
 */
export function updateRemoteConnectionStatus($container, apiId) {
  const $status = $container.find(".js-status-wrapper");

  return getApiStatus(apiId)
    .done(response => {
      $status.html(response);
      if (response.includes("invalid_token")) {
        $container.find(".oauth-connect-link").removeClass("hidden");
      } else {
        $container.find(".oauth-connect-link").addClass("hidden");
      }
    })
    .fail(() => {
      try {
        // Update the DOM with an error message.
        $status.html(
          `<span class="status-label api-error label label-danger"><i class="fa fa-exclamation-triangle spaced-right__sm fa-fw"></i>${
            window.PAGE.lang.errorText
          }</span>`
        );
      } catch (e) {
        // Since this code is loaded onto multiple pages, this error is thrown so the developer
        // will be reminded ot add that to the page.
        throw new Error(
          "Expected window to have Object `PAGE` with `lang.errorText` as a property"
        );
      }
    });
}

/*
 * Set up a modal to connect to the remote API.
 */
export const CONNECT_MODAL_SELECTOR = "remote-connect-wrapper";

/**
 * Initialize the modal code on the page and set up callbacks.
 * @param {function} connectedCB Callback to be called after the API has been connected.
 *                   This usually updates the UI.
 */
export function initConnectRemoteApi(connectedCB) {
  const wrapper = document.createElement("div");
  wrapper.classList.add("modal", "fade");
  wrapper.tabIndex = -1;
  wrapper.id = CONNECT_MODAL_SELECTOR;
  document.body.appendChild(wrapper);

  $(`#${CONNECT_MODAL_SELECTOR}`).on("show.bs.modal", function(event) {
    const wrapper = this;
    const $modal = $(wrapper);
    const btn = event.relatedTarget;
    const apiId = btn.dataset.apiId;

    /*
    In order to ensure proper setup of the modal, the content needs to be loaded dynamically
    for each remote API.  Here we are loading the content of the modal and then setting the
    src for the embedded iframe to point to the remote API.
     */
    $modal.load(`${window.TL.BASE_URL}remote_api/modal/${apiId}`, function() {
      const iframe = document.querySelector("#oauth-connect-frame");
      const url = iframe.dataset.url;
      iframe.src = `${url}${apiId}`;
    });

    /*
    Since the modal content is loaded from and iframe, clicking a button in the iframe can only close the
    modal window.  When the modal is closed the developer can pass a callback function to allow the updating of the UI.
     */
    $modal.on("hide.bs.modal", function(event) {
      if (event.currentTarget.dataset.result === "success") {
        if (typeof connectedCB === "function") {
          connectedCB(apiId);
        } else {
          // If no callback, reload the current page, without using the cache,
          // to ensure that everything gets updated.
          window.location.reload(true);
        }
      }
    });
  });
}
