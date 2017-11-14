import $ from "jquery";
import moment from "moment";
import "../../../../vendor/plugins/jquery/daterangepicker";
import { FILTERS, SAMPLE_EVENTS } from "../constants";

const $organismFilter = $("#js-organism");
const $nameFilter = $("#js-name");
const $dateRangeFilter = $("#js-daterange");

/*
Set up the date range filter.
This is based of off jquery date range picker (http://www.daterangepicker.com/)
 */
const dateRangePicker = $dateRangeFilter
  .daterangepicker({
    startDate: $("[name=startDate]").val(),
    endDate: $("[name=endDate]").val(),
    autoUpdateInput: false,
    locale: {
      cancelLabel: "Clear"
    },
    showDropdowns: true,
    ranges: {
      [window.PAGE.i18n.dateFilter.month]: [
        moment().subtract(1, "month"),
        moment()
      ],
      [window.PAGE.i18n.dateFilter.months3]: [
        moment().subtract(3, "month"),
        moment()
      ],
      [window.PAGE.i18n.dateFilter.months6]: [
        moment().subtract(6, "month"),
        moment()
      ],
      [window.PAGE.i18n.dateFilter.year]: [
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
    $(this).val(
      `${picker.startDate.format(
        window.PAGE.i18n.dateFilter.format
      )} - ${picker.endDate.format(window.PAGE.i18n.dateFilter.format)}`
    );
  })
  .on("cancel.daterangepicker", function() {
    $(this).val("");
  });

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
    const dateranges = dateRangePicker.data("daterangepicker");
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
