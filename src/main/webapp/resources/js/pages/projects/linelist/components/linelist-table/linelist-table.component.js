import {dom} from './../../../../../utilities/datatables.utilities';

function controller(DTOptionsBuilder,
                    DTColumnBuilder,
                    LinelistService,
                    $scope,
                    $compile) {
  const $ctrl = this;
  $ctrl.$onInit = () => {
    $ctrl.templates = LinelistService.getTemplates();
  };

  $ctrl.$postLink = () => {
    $ctrl.currentTemplate = 0;
  };

  $ctrl.dtOptions = DTOptionsBuilder
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
    fields="$ctrl.dtColumns"
    templates="$ctrl.templates"
    active-template="$ctrl.currentTemplate"
    on-toggle-field="$ctrl.toggleColumn($event)"
    on-save-template="$ctrl.saveTemplate($event)"
    on-get-template-fields="$ctrl.getTemplateFields($event)">
</metadata-component>
`;
        $compile(div)($scope);
      }
    });

  $ctrl.dtColumns = this.headers.map(header => {
    const col = DTColumnBuilder
      .newColumn(header)
      .withTitle(header)
      .renderWith(data => {
        // This is where any custom rendering logic should go.
        // example formatting date columns.
        return data.value;
      });
    col.visible = true;
    return col;
  });

  $ctrl.saveTemplate = $event => {
    const {templateName, fields} = $event;

    return LinelistService
      .saveTemplate({url: $ctrl.savetemplateurl, fields, name: templateName})
      .then(result => {
        // TODO: (Josh | 2017-02-15) This will be completed in the next merge request
        console.log(result);
      });
  };

  $ctrl.getTemplateFields = $event => {
    const {templateId} = $event;

    // Make sure that the current template index is indicated since the metadata component
    // will be redrawn.
    $ctrl.currentTemplate = $ctrl.templates.findIndex(template => {
      return template.id === templateId;
    });

    return LinelistService
      .getTemplateFields({templateId, url: $ctrl.gettemplatefieldsurl})
      .then(columns => {
        const oldCols = Array.from($ctrl.dtColumns);
        const newCols = [];

        columns.forEach(col => {
          for (let i = 0; i < oldCols.length; i++) {
            const c = oldCols[i];
            if (c.sTitle === col.label) {
              c.visible = true;
              newCols.push(c);
              oldCols.splice(i, 1);
              break;
            }
          }
        });
        oldCols.forEach(col => {
          col.visible = false;
        });
        $ctrl.dtColumns = [...newCols, ...oldCols];
        return $ctrl.dtColumns;
      });
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
    savetemplateurl: '@',
    gettemplatefieldsurl: '@'
  },
  controller
};
