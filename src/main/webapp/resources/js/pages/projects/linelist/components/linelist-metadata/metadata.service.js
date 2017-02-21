export function MetadataService($resource, $window,
                                $httpParamSerializerJQLike) {
  return $resource($window.PAGE.urls.saveTemplate, {
    id: '@id'
  }, {
    save: {
      method: 'POST',
      transformRequest: function(data) {
        return $httpParamSerializerJQLike(data);
      }
    }
  });
}

MetadataService.$inject = [
  '$resource',
  '$window',
  '$httpParamSerializerJQLike'
];
