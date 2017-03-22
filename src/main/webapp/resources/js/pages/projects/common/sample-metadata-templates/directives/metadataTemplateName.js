/**
 * Angular Directive to check to see if their is an existing template with the same name
 *
 * @return {object} directive actually returns whether the input is valid.
 */
export function metadataTemplateName() {
  return {
    require: 'ngModel',
    scope: {
      existing: '=existingTemplates'
    },
    link($scope, $elm, $attrs, $ctrl) {
      console.debug($scope);
      // existingTemplates is an attribute on the input value that passes a reference to a list
      // of MetadataTemplate names that already exist on this project.
      if (!Array.isArray($scope.existing)) {
        return true;
      }

      // This acts as an input validator testing to see if the template name the user entered
      // already exists in the project.  Returns a boolean value whether the name exists or not.
      $ctrl.$validators.unique = (modelValue, viewValue) => {
        return !$scope.existing
          .find(template => {
            return template.name === viewValue;
          });
      };
    }
  };
}
