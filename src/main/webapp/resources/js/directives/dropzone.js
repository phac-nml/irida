/**
 * @file AngularJS Directive for interacting with the Dropzone library.
 */
import Dropzone from 'dropzone';

// Prevent Dropzone to auto-magically finding itself in before it is needed.
Dropzone.autoDiscover = false;

const previewTemplate = `<span style='display: none'></span>`;

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
    previewTemplate,
    dictDefaultMessage: scope.message
  });

  console.log('hello');

  // Update event handlers
  // Unwraps the function as it is needed to be passed parameters later;
  if (typeof scope.onSuccess === 'function') {
    const sFn = scope.onSuccess();
    dz.on('success', sFn);
  }
  if (typeof scope.onComplete === 'function') {
    const cFn = scope.onComplete();
    dz.on('complete', cFn);
  }
  if (typeof scope.onError === 'function') {
    const eFn = scope.onError();
    dz.on('error', eFn);
  }
};

/**
 * Attributes expected on the dropzone tag
 */
const scope = {
  url: '@',
  message: '@',
  onSuccess: '&?',
  onComplete: '&?',
  onError: '&?',
  maxFiles: '@?',
  acceptedFiles: '@?'
};

const template = `
  <button class='btn btn-primary'>
    <span class='fa fa-upload'></span>&nbsp;
    Add New File
  </button>
`;

/**
 * Angular directive for Dropzone.js allowing a drag and drop interface for uploading
 * files to the server.
 *  Example:
 *    <dropzone data:url='@{url}'
 *          on-success='successCallback'
 *          on-complete='completionCallback' />
 * @return {object} {{restrict: string, scope: {url: string}, link: (function(*, *, *))}}
 */
const dropzone = () => {
  return {
    scope,
    link,
    restrict: 'E',
    replace: true,
    template
  };
};

export default dropzone;
