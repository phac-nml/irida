/**
 * @file AngularJS Directive for interacting with the Dropzone library.
 */
import Dropzone from "dropzone";

// Prevent Dropzone to auto-magically finding itself in before it is needed.
Dropzone.autoDiscover = false;

/**
 * Link function for the dropzone directive.
 * @param {object} scope - AngularJS dom scope
 * @param {array} element - element directive is attached to
 */
const link = (scope, element) => {
  // Initialize the dropzone.
  const dz = new Dropzone(element[0], {
    url: scope.url,
    maxFiles: scope.maxFiles,
    acceptedFiles: scope.acceptedFiles,
    dictDefaultMessage: scope.message
  });

  // Update event handlers
  // Unwraps the function as it is needed to be passed parameters later;
  if (typeof scope.onSuccess === "function") {
    const sFn = scope.onSuccess();
    dz.on('success', sFn);
  }
  if (typeof scope.onComplete === "function") {
    const cFn = scope.onComplete();
    dz.on('complete', cFn);
  }
  if (typeof scope.onError === "function") {
    const eFn = scope.onError();
    dz.on('error', eFn);
  }
};

/**
 * Attributes expected on the dropzone tag
 * @type {{url: string, onSuccess: function, onComplete: function, onError: function}}
 */
const scope = {
  url: "@",
  message: "@",
  onSuccess: "&?",
  onComplete: "&?",
  onError: "&?",
  maxFiles: "@?",
  acceptedFiles: "@?"
};

/**
 * Angular directive for Dropzone.js allowing a drag and drop interface for uploading
 * files to the server.
 *  Example:
 *    <dropzone data:url="@{url}"
 *          on-success="successCallback"
 *          on-complete="completionCallback" />
 * @return {object} {{restrict: string, scope: {url: string}, link: (function(*, *, *))}}
 */
const dropzone = () => {
  return {
    scope,
    link,
    restrict: "E",
    replace: true,
    template: `<form class="dropzone"></form>`
  };
};

export default dropzone;
