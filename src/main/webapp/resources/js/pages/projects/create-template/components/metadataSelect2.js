require('select2');
require('style!select2/dist/css/select2.css');
require('style!select2-bootstrap-theme/dist/select2-bootstrap.css');

export function metadataSelect2() {
  return {
    scope: {
      label: '='
    },
    template: `
    <select 
        ng-model="label"
        type="text" 
        class="form-control" ></select>
    `,
    link($scope, $element, $attrs) {
      $element.select2({
        theme: 'bootstrap',
        minimumInputLength: 2,
        ajax: {
          url: '/projects/4/sample-metadata/fields',
          dataType: 'json',
          data(params) {
            return {
              query: params.term
            };
          },
          processResults(data) {
            const results = [];
            data.forEach(item => {
              item.text = item.label;
              results.push(item);
            });
            return {results};
          },
          cache: true
        }
      }).on('select2:select', event => {
        $scope.label = event.params.data;
      });
    }
  };
}
