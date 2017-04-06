/**
 * Angular service for getting metadata fields
 * @param {object} $resource angular resource object.
 * @param {object} $window angular window object
 * @return {*} resource to handle metadata fields.
 * @constructor
 */
export function MetadataFieldService($resource, $window) {
  return $resource($window.PAGE.urls.fields, {
    query: {
      method: 'get', isArray: true
    }
  });
}

MetadataFieldService.$inject = [
  '$resource',
  '$window'
];
