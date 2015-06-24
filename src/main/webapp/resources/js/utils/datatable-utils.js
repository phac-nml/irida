/*exported datatable*/
var datatable = (function(moment, tl, page) {
  'use strict';
  /**
   * Format the date in a table.
   * Requires the page to have the date format object.
   * @param date Date from server.
   * @returns {*}
   */
  function formatDate(date) {
    if (moment !== undefined && tl.date && tl.date.moment.short) {
      return '<div><span style="display: none !important;">' + date + '</span>' + moment(date).format(tl.date.moment.short) + '</div>';
    } else {
      return new Date(date);
    }
  }

  /**
   * Translate text from the server
   * @param data text to be translated
   * @returns {*}
   */
  function i18n(data) {
    if (page && page.lang && page.lang[data]) {
      return page.lang[data];
    } else {
      return data;
    }
  }

  /**
   * Create a link button to the IRIDA thing
   * @param data column data
   * @param type type of data
   * @param full full object for the row.
   * @returns {*}
   */
  function createItemButton(data, type, full) {
    if (tl && full.link && tl.BASE_URL) {
      return '<a class="item-link btn btn-default table-cell-override btn-xs" title="' + data + '" href="' + tl.BASE_URL + full.link + '">' + data + '</a>';
    } else {
      return data;
    }
  }

  function forceContentSize(data, type, full) {
    if (data && data.length > 0) {
      return '<div class="table-cell-override" title="' + data + '">' + data + '</div>';
    } else {
      return data;
    }
  }

  /**
   * Called when the datatable is drawn.
   *    Resizes the table
   *    Fixes bootstrap issues
   */
  function tableDrawn() {
    updateFilters();
    resizeTable();
  }

  /**
   * Updates the filters and search field to use the appropriate bootstrap classes.
   */
  function updateFilters() {
    var filters = document.querySelectorAll('input.dandelion_column_filter, select.dandelion_column_filter');
    if (filters && filters.length) {
      [].forEach.call(filters, function (el) {
        el.setAttribute('style','width:200px');
        var classList = el.classList;
        if (!classList.contains('form-control')) {
          el.classList.add('form-control');
        }
        if (!classList.contains('input-sm')) {
          el.classList.add('input-sm');
        }
        if(el.type === 'text'){
          el.value = '';
        }
        else if(el.type === 'select-one') {
         el.options[0] = new Option('-', '', true, true);
        }
      });
    }
  }

  /**
   * Resizes the datatable to be full screen.
   */
  function resizeTable() {
    var h = window.innerHeight,
      scrollBody = document.getElementsByClassName('dataTables_scrollBody')[0],
      scrollBodyClientRect = scrollBody.getBoundingClientRect(),
      table = scrollBody.getElementsByTagName('table')[0],
      tableClientRect = table.getBoundingClientRect();
    if (tableClientRect.bottom > h) {
      scrollBody.style.height = h - scrollBodyClientRect.top - 60 + 'px';
    } else {
      // + 1 to prevent the scrollbar from appearing
      scrollBody.style.height = tableClientRect.bottom - scrollBodyClientRect.top + 1 + 'px';
    }
  }

  window.onresize = resizeTable;

  return {
    formatDate: formatDate,
    i18n: i18n,
    createItemButton: createItemButton,
    forceContentSize: forceContentSize,
    tableDrawn: tableDrawn
  };
})(window.moment, window.TL, window.PAGE);
