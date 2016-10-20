const $ = require('jquery');
require('select2');
require('style!select2/dist/css/select2.min.css');
require('style!select2-bootstrap-theme/dist/select2-bootstrap.min.css');

const Select2Basic = () => {
  return {
    restrict: 'A',
    link($scope, $elm) {
      $($elm).select2();
    }
  };
};

export default Select2Basic;
