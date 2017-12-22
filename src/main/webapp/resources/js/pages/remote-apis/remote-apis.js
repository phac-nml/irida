function getApiStatus(apiId) {
  return $.ajax({
    url: `${window.TL.BASE_URL}remote_api/status/${apiId}`,
    type: "GET",
    dataType: "html"
  });
}

export function updateRemoteConnectionStatus($container, apiId) {
  const $status = $container.find(".status-wrapper");

  return getApiStatus(apiId)
    .success(response => {
      $status.html(response);
      if (response.includes("invalid_token")) {
        $container.find(".oauth-connect-link").removeClass("hidden");
      } else {
        $container.find(".oauth-connect-link").addClass("hidden");
      }
    })
    .error(() => {
      try {
        $status.html(
          `<span class="status-label api-error label label-danger"><i class="fa fa-exclamation-triangle spaced-right__sm fa-fw"></i>${
            window.PAGE.lang.errorText
          }</span>`
        );
      } catch (e) {
        new Error(
          "Expected window to have Object `PAGE` with `lang.errorText` as a property"
        );
      }
    });
}

export const CONNECT_MODAL_SELECTOR = "remote-connect-wrapper";
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

    $modal.load(`${window.TL.BASE_URL}remote_api/modal/${apiId}`, function() {
      const iframe = document.querySelector("#oauth-connect-frame");
      const url = iframe.dataset.url;
      iframe.src = `${url}${apiId}`;
    });

    $modal.on("hide.bs.modal", function(event) {
      if (
        event.currentTarget.dataset.result === "success" &&
        typeof connectedCB === "function"
      ) {
        connectedCB(apiId);
      }
    });
  });
}
