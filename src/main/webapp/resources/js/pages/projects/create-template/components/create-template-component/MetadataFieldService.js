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
