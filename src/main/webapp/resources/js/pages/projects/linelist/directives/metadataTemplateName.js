/**
 * Angular Directive to check to see if their is an existing template with the same name
 * @return {object} directive actually returns whether the input is valid.
 */
export function metadataTemplateName() {
  return {
    require: 'ngModel',
    scope: {
      existing: '=existingTemplates'
    },
    link($scope, $elm, $attrs, $ctrl) {
      if (Array.isArray($scope.existing)) {
        $ctrl.$validators.unique = (modelValue, viewValue) => {
          return !$scope.existing
            .find(template => {
              return template.name === viewValue;
            });
        };
      }
    }
  };
}
