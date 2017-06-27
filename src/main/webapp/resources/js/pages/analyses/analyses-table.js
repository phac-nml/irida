import "css/pages/analyses-list.css";
import "DataTables/datatables";
import {
  activateTooltips,
  createButtonCell,
  createDeleteBtn,
  createDownloadLink,
  createItemLink,
  dom,
  formatDateDOM,
  generateColumnOrderInfo,
  getHumanizedDuration
} from "DataTables/datatables-utilities";

import $ from "jquery";
import {deleteAnalysis} from "../analysis/analysis-service";

const COLUMNS = generateColumnOrderInfo();

/**
 * Create the state cell for the table.  This includes both the
 * state label and the percentage bar.
 * @param {object} full data for row object.
 * @return {string} of DOM representing cell.
 */
function createState(full) {
  const stateClasses = {
    COMPLETED: 'progress-bar-success',
    ERROR: 'progress-bar-danger'
  };

  let stateClass = '';
  if (stateClasses[full.submission.analysisState] !== null) {
    stateClass = stateClasses[full.submission.analysisState];
  }

  let percent = full.percentComplete;
  if (full.submission.analysisState === 'ERROR') {
    percent = 100;
  }
  return `
${full.analysisState}
<div class='progress analysis__state'>
  <div class='progress-bar ${stateClass}' 
       role='progressbar' aria-valuenow='${percent}' 
       aria-valuemin='0' aria-valuemax='100' 
       style='width:${percent}%;'>
  </div>
</div>`;
}

const table = $('#analyses').DataTable({
  processing: true,
  serverSide: true,
  ajax: window.PAGE.URLS.analyses,
  dom,
  order: [[COLUMNS.CREATED_DATE, 'desc']],
  columnDefs: [
    {
      targets: [COLUMNS.ANALYSIS_STATE],
      render(data, type, full) {
        return createState(full);
      }
    },
    {
      targets: [COLUMNS.NAME],
      render(data, type, full) {
        return createItemLink({
          url: `${window.PAGE.URLS.analysis}${full.id}`,
          label: data
        });
      }
    },
    {
      targets: [COLUMNS.CREATED_DATE],
      render: function(data) {
        return formatDateDOM({data});
      }
    },
    {
      targets: [COLUMNS.DURATION],
      render(data) {
        return getHumanizedDuration({date: data});
      }
    },
    {
      targets: [COLUMNS.BUTTONS],
      sortable: false,
      render(data, type, full) {
        const buttons = [];
        if ((full.submission.analysisState).localeCompare('COMPLETED') === 0) {
          const anchor = createDownloadLink({
            url: `${window.PAGE.URLS.download}${full.id}`,
            title: `${full.name}.zip`
          });
          buttons.push(anchor);
        }
        if (full.updatePermission) {
          const removeBtn = createDeleteBtn({
            id: full.id,
            name: full.name,
            toggle: 'modal',
            target: '#deleteConfirmModal'
          });
          buttons.push(removeBtn);
        }
        return createButtonCell(buttons);
      }
    }
  ],
  createdRow(row, full) {
    activateTooltips(row);
  }
});

/**
 * Set the state for the Analyses table filters.
 * @param {string} name of the analysis
 * @param {string} state of the workflow
 * @param {string} workflow identifier
 */
function setFilterState(name, state, workflow) {
  table.column(COLUMNS.NAME).search(name);
  table.column(COLUMNS.ANALYSIS_STATE).search(state);
  table.column(COLUMNS.WORKFLOW_ID).search(workflow).draw();
}

// Set up the delete modal
$('#deleteConfirmModal')
  .on('shown.bs.modal', () => {
    document.querySelector('#delete-analysis-button').focus();
  })
  .on('show.bs.modal', function(e) {
    const button = $(e.relatedTarget); // The button that triggered the modal.

    $(this).find('#delete-analysis-button').off('click').on('click', () => {
      deleteAnalysis({id: button.data('id')}).then(
        result => {
          window.notifications.show({msg: result.result});
          table.ajax.reload();
        },
        () => {
          window.notifications.show({
            msg: window.PAGE.i18n.unexpectedDeleteError,
            type: 'error'
          });
        }
      );
    });
  });

// Set up clear filters button
document.querySelector('#clear-filters').addEventListener('click', () => {
  table.search('');
  setFilterState('', '', '');
});

// Set up the filter modal
const nameFilter = document.querySelector('#nameFilter');
const stateFilter = document.querySelector('#analysisStateFilter');
const workflowFilter = document.querySelector('#workflowIdFilter');
$('#filterModal')
  .on('shown.bs.modal', () => {
    nameFilter.value = table.column(COLUMNS.NAME).search();
    stateFilter.value = table.column(COLUMNS.STATE).search();
    workflowFilter.value = table.column(COLUMNS.WORKFLOW_ID).search();
    nameFilter.focus();
  })
  .on('show.bs.modal', function() {
    // When the filter modal is opened, set up the click
    // handlers for all filter properties.
    $(this).find('#filterAnalysesBtn').off('click').on('click', () => {
      const name = nameFilter.value;
      const state = stateFilter.value;
      const workflow = workflowFilter.value;
      setFilterState(name, state, workflow);
    });
  });

/**
 * Generate the advanced filter buttons after the DataTables
 * has been initialized.
 */
(function createFilterButton() {
  const $wrapper = $('#filterBtnWrapper');
  const $btn = $wrapper.find('.btn-toolbar');
  $(document).remove($wrapper);

  // Adjust the default search field;
  $('#analyses_filter')
    .parent()
    .append($btn);
})();
