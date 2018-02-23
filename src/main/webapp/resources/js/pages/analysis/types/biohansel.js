import $ from "jquery";
import "../../../vendor/datatables/datatables";
import {
  createItemLink,
  generateColumnOrderInfo,
  tableConfig,
  wrapCellContents
} from "./../../../utilities/datatables-utilities";

export function BioHanselController(analysisService){
  const vm = this;
  const $table = $('#tech_results_table');
  const COLUMNS = generateColumnOrderInfo();


    const config = Object.assign({}, tableConfig,
    {
          serverSide: false,
          ajax: $table.data("url"),
          columnDefs: [
            {
                targets: COLUMNS.QC_MESSAGE,
                width: "45%"
            },
            {
                targets: COLUMNS.QC_STATUS,
                width: "15%"
            },
            {
                targets: COLUMNS.SUBTYPE,
                width: "15%"
            },
            {
                targets: COLUMNS.SAMPLE,
                width: "25%"
            }
          ]
    });
    //Creating the DataTable used to show Bio Hansel's results.
    const $dt = $table.DataTable(config);
}