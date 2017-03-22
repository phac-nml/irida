const angular = require('angular');

/**
 * Angular Directive to display errors on an input field.
 * Add the directive as an attribute on the surrounding `class="form-group"` div.
 * @return {object} Angular directive
 *
 * ng-model-options="{debounce: 350}"
 */
export function showValidation() {
  return {
    restrict: 'A',
    require: '^form',
    link($scope, $elem, $attrs, $formCtrl) {
      // Find the input's 'name' attr
      const inputEl = $elem[0].querySelector('[name]');

      // Convert the native input to an angular element
      const ngEl = angular.element(inputEl);

      // Get the name on of the input so we kow the property to check on the controller
      const elName = ngEl.attr('name');

      // Update the inputs model-options to only update the angular model after 350ms.
      $formCtrl[elName].$options.$$options.debounce = 350;

      // Watch for changes to the input and apply the error if required.
      $scope.$watch(() => {
        return inputEl.value;
      }, (newVal, oldVal) => {
        if (newVal !== oldVal) {
          if ($formCtrl[elName].$invalid) {
            $elem.addClass('has-error');
            $elem.removeClass('has-success');
          } else if ($formCtrl[elName].$valid) {
            $elem.removeClass('has-error');
            $elem.addClass('has-success');
          }
        }
      });
    }
  };
}
