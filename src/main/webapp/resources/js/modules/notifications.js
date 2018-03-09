import "noty";

const defaultConfig = {
  theme: "metroui",
  timeout: 3500, // [integer|boolean] delay for closing event in milliseconds. Set false for sticky notifications
  progressBar: true,
  type: "success",
  closeWith: ["click"], // String array with 'click' or 'button' or both
  animation: {
    open: "animated bounceInRight",
    close: "animated bounceOutRight"
  },
  text: ""
};

export function showNotification(params) {
  return noty(Object.assign({}, defaultConfig, params));
}

// TODO: Remove this after all notification usages are through a webpack bundle.
window.notifications = (function() {
  return { show: showNotification };
})();
