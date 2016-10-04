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
    header: '<',
    idColumn: '='
  },
  require: {
    parent: '^displayColumnHeaders'
  },
  template
};

export default headerItem;
