export function MetadataTemplateService($resource) {
  return $resource(window.PAGE.urls.metadata, {
    id: '@id'
  });
}

MetadataTemplateService.$inject = ['$resource'];
