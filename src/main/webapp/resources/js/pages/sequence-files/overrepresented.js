import $ from "jquery";
import "./../../vendor/datatables/datatables";

$("#orTable").dataTable({
  dom: "<'top'il>rt<'bottom'p><'clear'>",
  bFilter: false,
  bSort: false
});
