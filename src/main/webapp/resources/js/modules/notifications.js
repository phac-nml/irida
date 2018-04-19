import Noty from "noty";
import "noty/src/noty.scss";
import "noty/src/themes/relax.scss";

/**
 * Default noty notification library options object for initialization of notification of success.
 *
 * See https://ned.im/noty/#/options for more info
 *
 * @type {{theme: string, timeout: number, progressBar: boolean, type: string, closeWith: string[], animation: {open: string, close: string}, text: string}}
 */
const defaultConfig = {
  theme: "relax",
  timeout: 3500,
  layout: "topCenter",
  progressBar: true,
  type: "success",
  closeWith: ["click"], // String array with 'click' or 'button' or both
  animation: {
    open: "fadeInDown",
    close: "fadeOutUp"
  },
  text: ""
};

/**
 * Default noty notification library options object for initialization of notification of an error.
 *
 * See https://ned.im/noty/#/options for more info
 *
 * @type {{theme: string, timeout: boolean, progressBar: boolean, type: string, closeWith: string[], animation: {open: string, close: string}, text: string}}
 */
const defaultErrorConfig = Object.assign({}, defaultConfig, {
  timeout: false,
  progressBar: false,
  type: "error",
  closeWith: ["button"]
});

/**
 * Show UI notification with default type "success" that is dismissed after 3.5 seconds or onClick event.
 * @param params Object with `text` key containing notification text, and overrides to default parameters.
 */
export function showNotification(params) {
  return new Noty(Object.assign({}, defaultConfig, params)).show();
}

/**
 * Show UI notification with error information.
 *
 * Default type "error", close of notification on close button click, no timeout so user needs to dismiss the error by clicking the close button (enables user to copy error text for reporting of issues).
 *
 * @param params Object with `text` key containing error info, and overrides to default parameters.
 */
export function showErrorNotification(params) {
  return noty(Object.assign({}, defaultErrorConfig, params));
}

// TODO: Remove this after all notification usages are through a webpack bundle.
window.notifications = (function() {
  return { show: showNotification, showError: showErrorNotification };
})();
