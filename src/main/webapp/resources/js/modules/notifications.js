import "noty";

export function showNotification({text, type = "success"}) {
  return noty({
    theme: "metroui",
    timeout: 3500, // [integer|boolean] delay for closing event in milliseconds. Set false for sticky notifications
    progressBar: true,
    animation: {
      open: 'animated bounceInRight',
      close: 'animated bounceOutRight'
    },
    type,
    text
  });
}

window.notifications = (function () {
  return {show: showNotification};
})();