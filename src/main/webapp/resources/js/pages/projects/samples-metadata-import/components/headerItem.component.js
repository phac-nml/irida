/**
 * @file Angular component for displaying metadata headers with their selection input.
 * This component expects to be underneath the  selectSampleNameColumnComponent`
 */
const template = `
<div>
  <div class="radio">
    <label>
      <input type="radio" 
         ng-value="$ctrl.header" 
         ng-model="$ctrl.parent.idColumn" />
         {{ $ctrl.header }}
    </label>
  </div>
</div>
`;

const headerItem = {
  bindings: {
    header: "<",
    idColumn: "="
  },
  require: {
    parent: "^selectSampleNameColumnComponent"
  },
  template
};

export default headerItem;
