import Dropzone from 'dropzone';

// Prevent Dropzone to auto-magically finding itself in before it is needed.
Dropzone.autoDiscover = false;

/**
 * Link function for the dropzone directive.
 * @param {object} scope - AngularJS dom scope
 * @param {array} element - element directive is attached to
 */
const link = (scope, element) => {
  // Unwraps the function and is needed to passed parameters later;
  scope.onSuccess = scope.onSuccess();
  scope.onComplete = scope.onComplete();
  scope.onError = scope.onError();

  // Initialize the dropzone.
  const dz = new Dropzone(element[0], {
    url: scope.url
  });
  // Update event handlers
  dz.on('success', scope.onSuccess);
  dz.on('complete', scope.onComplete);
  dz.on('error', scope.onError);
};

/**
 * Attributes expected on the dropzone tag
 * @type {{url: string, onSuccess: function, onComplete: function, onError: function}}
 */
const scope = {
  url: "@",
  onSuccess: "&",
  onComplete: "&",
  onError: "&"
};

/**
 * Angular directive for Dropzone.js allowing a drag and drop interface for uploading
 * files to the server.
 *  Example:
 *    <dropzone th:url="@{url}"
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
