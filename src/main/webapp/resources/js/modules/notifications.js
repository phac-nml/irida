import Noty from "noty";
import "noty/src/noty.scss";
import "noty/src/themes/relax.scss";

const defaultConfig = {
  theme: "relax",
  timeout: 3500,
  layout: "topCenter",
  progressBar: true,
  type: "success",
  closeWith: ["click"], // String array with 'click' or 'button' or both
  animation: {
    open: "animated fadeIn",
    close: "animated fadeOut"
  },
  text: ""
};

export function showNotification(params) {
  return new Noty(Object.assign({}, defaultConfig, params)).show();
}

// TODO: Remove this after all notification usages are through a webpack bundle.
window.notifications = (function() {
  return { show: showNotification };
})();
