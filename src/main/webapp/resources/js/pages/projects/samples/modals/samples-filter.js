import $ from "jquery";
import moment from "moment";
import "../../../../vendor/plugins/jquery/daterangepicker";
import { FILTERS, SAMPLE_EVENTS } from "../constants";

const $organismFilter = $("#js-organism");
const $nameFilter = $("#js-name");
const $dateRangeFilter = $("#js-daterange");

function formatDateRangeInput(start, end) {
  $dateRangeFilter.val(
    `${start.format(i18n("project.sample.filter.date.format"))} - ${end.format(
      i18n("project.sample.filter.date.format")
    )}`
  );
}

/*
Set up the date range filter.
This is based of off jquery date range picker (http://www.daterangepicker.com/)
 */
$dateRangeFilter
  .daterangepicker({
    autoUpdateInput: false,
    locale: {
      cancelLabel: "Clear"
    },
    showDropdowns: true,
    ranges: {
      [i18n("project.sample.filter.date.month")]: [
        moment().subtract(1, "month"),
        moment()
      ],
      [i18n("project.sample.filter.date.months3")]: [
        moment().subtract(3, "month"),
        moment()
      ],
      [i18n("project.sample.filter.date.months6")]: [
        moment().subtract(6, "month"),
        moment()
      ],
      [i18n("project.sample.filter.date.year")]: [
        moment().subtract(1, "year"),
        moment()
      ]
    }
  })
  .on("apply.daterangepicker", function(ev, picker) {
    /*
    Call the the apply button is clicked.
    Formats the dates into human readable form.  This is required since we disabled
    the update of the input field (autoUpdateInput: false) to allow for an empty field to begin with.
     */
    formatDateRangeInput(picker.startDate, picker.endDate);
  })
  .on("cancel.daterangepicker", function() {
    $(this).val("");
  });

/*
Initialize the daterange picker if valiues were already set
 */
const startInputVal = $("[name=startDate]").val();
const endInputVal = $("[name=endDate]").val();
if (!isNaN(Date.parse(startInputVal)) || !isNaN(Date.parse(endInputVal))) {
  const start = moment(new Date(startInputVal));
  $dateRangeFilter.data("daterangepicker").setStartDate(start);
  const end = moment(new Date(endInputVal));
  $dateRangeFilter.data("daterangepicker").setEndDate(end);
  formatDateRangeInput(start, end);
}

$("#js-do-filter").on("click", function() {
  const filters = {};
  /*
  Apply the filters to the table.
   */

  // Check to see if the name filter needs to be applied.
  if ($nameFilter.val()) {
    filters[FILTERS.FILTER_BY_NAME] = $nameFilter.val();
  }

  // Check to see if the organism filter needs tobe applied.
  if ($organismFilter.val()) {
    filters[FILTERS.FILTER_BY_ORGANISM] = $organismFilter.val();
  }

  // Check to see if the date range filter needs to be applied.
  if ($dateRangeFilter.val()) {
    const dateranges = $dateRangeFilter.data("daterangepicker");
    const startDate = dateranges.startDate.toDate().getTime();
    const endDate = dateranges.endDate.toDate().getTime();
    filters[FILTERS.FILTER_BY_EARLY_DATE] = startDate;
    filters[FILTERS.FILTER_BY_LATEST_DATE] = endDate;
  }

  /*
  Close the modal and return the values so the table can be updated.
   */
  $("#js-filter-modal-wrapper").trigger(
    SAMPLE_EVENTS.SAMPLE_FILTER_CLOSED,
    filters
  );
});
