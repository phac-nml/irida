/**
 * Directive for templateInputComponent to ensure that a name is not repeated.
 * @return {object} if the input is valid
 */
export function noRepeatName() {
  return {
    require: 'ngModel',
    link(scope, elm, attrs, ctrl) {
      ctrl.$validators.noRepeatName = value => {
        const item = scope.$parent.$ctrl.template.list[attrs.index];

        // This check was required to prevent a failure when completing a
        // drag and drop event.  If the values are the same, then the item
        // was moved.
        if (item.previous && item.previous === value) {
          return true;
        }

        // Create a copy of the original list of fields and get the
        // label value.
        const list = Array.from(scope.$parent.$ctrl.template.list)
          .map(name => name.label);
        // Remove the reference to the current field, since we don't want
        // to get an index of that one.
        list.splice(attrs.index, 1);
        // Store a reference to the current value for checking during drag
        // and drop events.
        item.previous = value;
        // If the item is not in the list than the field is valid.
        return list.indexOf(value) === -1;
      };
    }
  };
}
