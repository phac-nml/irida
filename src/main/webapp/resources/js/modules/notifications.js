import {
  MESSAGE_EVENT,
  NOTIFICATION_EVENT
} from "../components/notifications/Notifications";

/**
 * Show UI notification with default type "success" that is dismissed after 3.5 seconds or onClick event.
 * @param params Object with `text` key containing notification text, and overrides to default parameters.
 */
export function showNotification(params) {
  const event = new CustomEvent(MESSAGE_EVENT, {
    detail: {
      text: params.text.trim(),
      type: params.type
    }
  });
  window.dispatchEvent(event);
}

/**
 * Show UI notification with error information.
 *
 * Default type "error", close of notification on close button click, no timeout so user needs to dismiss the error by clicking the close button (enables user to copy error text for reporting of issues).
 *
 * @param params Object with `text` key containing error info, and overrides to default parameters.
 */
export function showErrorNotification(params) {
  showNotification({ text: params.text, type: "error" });
}

/**
 * Show UI notification when a value has been changed and provide the capability to undo it.
 * @param {object} params - overwrite the default configuration
 * @param {function} callback - undo callback
 */
export function showUndoNotification(params, callback) {
  const event = new CustomEvent(NOTIFICATION_EVENT, {
    detail: { callback, text: params.text }
  });
  window.dispatchEvent(event);
}

// TODO: Remove this after all notification usages are through a webpack bundle.
window.notifications = (function() {
  return { show: showNotification, showError: showErrorNotification };
})();
