import Dropzone from 'dropzone';

// Prevent Dropzone to auto-magically finding itself in before it is needed.
Dropzone.autoDiscover = false;

/**
 * Angular directive for Dropzone.js allowing a drag and drop interface for uploading
 * files to the server.
 *  Example:
 *    <form action="" th:action="@{url}"
 *          id="specialId"
 *          class="dropzone"
 *          on-success="successCallback"
 *          on-complete="completionCallback" />
 * @return {object} {{restrict: string, scope: {url: string}, link: (function(*, *, *))}}
 */
const dropzone = () => {
  return {
    restrict: "C",
    scope: {
      onSuccess: "&",
      onComplete: "&"
    },
    link: (scope, element) => {
      // Initialize the dropzone.
      const dz = new Dropzone(element[0], {});
      // Unwraps the function and is needed to passed parameters later;
      scope.onSuccess = scope.onSuccess();
      scope.onComplete = scope.onComplete();

      dz.on('success', scope.onSuccess);
      dz.on('complete', scope.onComplete);
    }
  };
};

export default dropzone;
