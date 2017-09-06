import "./../../../vendor/datatables/datatables";
import {
  tableConfig,
  generateColumnOrderInfo,
  createItemLink
} from "../../../utilities/datatables-utilities";
import {formatDate} from "../../../utilities/date-utilities";

const COLUMNS = generateColumnOrderInfo();
console.log(COLUMNS);

const $table = $("#project-samples");
const url = $table.data("url");

const config = Object.assign({}, tableConfig, {
  ajax: url,
  columnDefs: [
    {
      targets: [COLUMNS.SAMPLE_SAMPLE_NAME],
      render(data, type, full) {
        return createItemLink({
          url: `${window.TL.BASE_URL}${full.projectId}/${full.id}`,
          label: full.sampleName
        });
      }
    },
    {
      targets: [COLUMNS.PROJECT_LABEL],
      render(data, type, full) {
        return createItemLink({
          url: `${window.TL.BASE_URL}projects/${full.projectId}`,
          label: full.projectName
        });
      }
    },
    {
      targets: [COLUMNS.CREATED_DATE, COLUMNS.MODIFIED_DATE],
      render(data) {
        return `<time>${formatDate({date:data})}</time>`
      }
    }
  ]
});

const $dt = $table.DataTable(config);
