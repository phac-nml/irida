import $ from "jquery";
import moment from "moment";
import "../../../../vendor/plugins/jquery/daterangepicker";
import { FILTERS, SAMPLE_EVENTS } from "../constants";

const $organismFilter = $("#js-organism");
const $nameFilter = $("#js-name");
const $dateRangeFilter = $("#js-daterange");

const dateRangePicker = $dateRangeFilter
  .daterangepicker({
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
    $(this).val(
      picker.startDate.format(window.PAGE.i18n.dateFilter.format) +
      " - " +
      picker.endDate.format(window.PAGE.i18n.dateFilter.format)
    );
  })
  .on("cancel.daterangepicker", function(ev, picker) {
    $(this).val("");
  });

$("#js-do-filter").on("click", function() {
  const filters = {};

  if ($nameFilter.val()) {
    filters[FILTERS.FILTER_BY_NAME] = $nameFilter.val();
  }
  if ($organismFilter.val()) {
    filters[FILTERS.FILTER_BY_ORGANISM] = $organismFilter.val();
  }
  if ($dateRangeFilter.val()) {
    console.log($dateRangeFilter.val());
    const dateranges = dateRangePicker.data("daterangepicker");
    const startDate = dateranges.startDate.toDate().getTime();
    const endDate = dateranges.endDate.toDate().getTime();
    filters[FILTERS.FILTER_BY_EARLY_DATE] = startDate;
    filters[FILTERS.FILTER_BY_LATEST_DATE] = endDate;
  }

  $("#js-filter-modal-wrapper").trigger(
    SAMPLE_EVENTS.SAMPLE_FILTER_CLOSED,
    filters
  );
});