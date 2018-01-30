import Noty from "noty";
import "noty/src/noty.scss";
import "noty/src/themes/sunset.scss";

export function showNotification({ text, type = "success" }) {
  return new Noty({
    theme: "sunset",
    timeout: 3500, // [integer|boolean] delay for closing event in milliseconds. Set false for sticky notifications
    layout: "bottomRight",
    progressBar: true,
    type,
    text,
    animation: {
      open: "animated fadeInUp",
      close: "animated fadeOutDown"
    }
  }).show();
}

// TODO: Remove this after all notification usages are through a webpack bundle.
window.notifications = (function() {
  return { show: showNotification };
})();
