import {dom} from './../../../../../utilities/datatables.utilities';

function controller(DTOptionsBuilder,
                    DTColumnBuilder,
                    LinelistService,
                    $scope,
                    $compile) {
  const $ctrl = this;
  // This will be used by the child component to control which columns are visible
  this.fields = this.headers
    .map((header, index) => {
      return ({text: header, index, selected: true});
    });

  this.dtOptions = DTOptionsBuilder
    .fromFnPromise(() => {
      return LinelistService.getMetadata();
    })
    .withDOM(dom)
    .withScroller()
    .withOption('scrollX', true)
    .withOption('deferRender', true)
    .withOption('scrollY', '50vh')
    .withOption('scrollCollapse', true)
    .withColReorder()
    .withColReorderCallback(function() {
      $ctrl.parent.columnReorder(this.fnOrder());
    })
    .withOption('drawCallback', () => {
      // This adds the tools to handle meta data header hiding, template selection and saving.
      // Datatables will add this after the table is created to we need the $compile so that
      // angularjs can grab hold of it.
      const div = document.querySelector('.toolbar');
      // Make sure this only gets added once
      if (div.getElementsByTagName('metadata-component').length === 0) {
        div.innerHTML = `
<metadata-component 
    fields="$ctrl.fields" 
    on-toggle-field="$ctrl.toggleColumn($event)"
    on-save-template="$ctrl.saveTemplate($event)">
</metadata-component>
`;
        $compile(div)($scope);
      }
    });

  this.dtColumns = this.headers.map(header => {
    return DTColumnBuilder
      .newColumn(header)
      .withTitle(header)
      .renderWith(data => {
        // This is where any custom rendering logic should go.
        // example formatting date columns.
        return data.value;
      });
  });

  this.toggleColumn = $event => {
    const field = $event.field;
    this.dtColumns[field.index].visible = field.selected;
  };

  this.saveTemplate = $event => {
    const fields = $event.fields.map(field => field.text);
    console.log(fields);
    LinelistService
      .saveTemplate({url: this.savetemplateurl, fields, name: 'freds'})
      .then(result => console.log(result));
  };
}

controller.$inject = [
  'DTOptionsBuilder',
  'DTColumnBuilder',
  'LinelistService',
  '$scope',
  '$compile'
];

export const TableComponent = {
  template: `
<table datatable="" 
  class="table" 
  dt-options="$ctrl.dtOptions" 
  dt-columns="$ctrl.dtColumns">
</table>`,
  require: {
    parent: '^^linelist'
  },
  bindings: {
    headers: '<',
    savetemplateurl: '@'
  },
  controller
};
