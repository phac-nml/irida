/*eslint no-undef: 2*/
/*global Option:true*/
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
    if (moment !== undefined && date !== undefined && tl.date && tl.date.moment.short) {
      return '<div><span style="display: none !important;">' + date + '</span>' + moment(date).format(tl.date.moment.short) + '</div>';
    } else {
      return new Date(date);
    }
  }

  function formatSampleLink(data, type, full) {
    if(typeof full.sample === 'undefined'){
      return data;
    }
    else {
      return "<a class='btn btn-link' href='" + tl.BASE_URL + "projects/" + full.project.identifier + "/samples/" + full.sample.identifier + "'>" + data + "</a>";
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
   * Return the size of the list passed in the data param
   * @param data column data.  Should be a JSON list
   * @returns size of the data list
   */
  function displayListSize(data) {
    return data.length;
  }

  function forceContentSize(data) {
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
  }

  /**
   * Updates the filters and search field to use the appropriate bootstrap classes.
   */
  function updateFilters() {
    var filters = document.querySelectorAll('.yadcf-filter');
    if (filters && filters.length) {
      [].forEach.call(filters, function (el) {
        var classList = el.classList;
        if (!classList.contains('form-control')) {
          el.classList.add('form-control');
        }
        if (!classList.contains('input-sm')) {
          el.classList.add('input-sm');
        }
        if(el.type == 'text'){
          el.placeholder = '';
        }
        else if(el.tagName.toLowerCase() == 'select') {
         el.options[0] = new Option('', '', true, true);
        }
      });
    }
  }

  return {
    formatDate: formatDate,
    i18n: i18n,
    forceContentSize: forceContentSize,
    tableDrawn: tableDrawn,
    displayListSize: displayListSize,
    formatSampleLink: formatSampleLink
  };
})(window.moment, window.TL, window.PAGE);
