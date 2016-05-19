/*eslint no-undef: 2*/
/*global Option:true*/
/*exported datatable*/
var datatable = (function(moment, tl, page) {
  'use strict';

  function _getDate(date) {
    return moment(date).format(tl.date.moment.short);
  }

  function _getTime(date) {
    return moment(date).format(tl.date.moment.time);
  }

  function _createDateElement(date) {
    var wrapper = document.createElement("div"),
        dateSpan = document.createElement("span"),
        hiddenUnixSpan = document.createElement("span");

    // Wrap the div - so we can unwrap it to return
    wrapper.appendChild(dateSpan);

    // Add the unix time for sorting purposes;
    hiddenUnixSpan.classList.add("hidden");
    hiddenUnixSpan.innerText = date;
    dateSpan.appendChild(hiddenUnixSpan);

    // Add the formatted date
    var dateNode = document.createTextNode(_getDate(date));
    dateSpan.appendChild(dateNode);
    return wrapper.innerHTML;
  }

  function _createTimeElement(date) {
    var wrapper = document.createElement("div"),
        span = document.createElement("span"),
        faSpan = document.createElement("span");

      // Wrap the div - so we can unwrap it to return
      wrapper.appendChild(span);

    // Create the font-awesome icon for the clock
    faSpan.classList.add("fa");
    faSpan.classList.add("fa-clock-o");
    // Need some space before the icon
    span.appendChild(document.createTextNode(" "));
    span.appendChild(faSpan);
    span.appendChild(document.createTextNode(" "));

    var timeNode = document.createTextNode(' ' + _getTime(date));
    span.appendChild(timeNode);
    return wrapper.innerHTML;
  }

  function _createDateAndTimeElement(date) {
    var dateHTML = _createDateElement(date),
        timeHTML = _createTimeElement(date);
    return dateHTML + timeHTML;
  }

  /**
   * Format the date in a table.
   * Requires the page to have the date format object.
   * @param date Date from server.
   * @returns {*}
   */
  function formatDate(date) {
    if (moment !== undefined && date !== undefined && tl.date && tl.date.moment.short) {
      return _createDateElement(date);
    } else {
      return new Date(date);
    }
  }

  function formatDateWithTime(date) {
    if (moment !== undefined && date !== undefined && tl.date && tl.date.moment.short) {
      return _createDateAndTimeElement(date);
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
    formatDateWithTime: formatDateWithTime,
    i18n: i18n,
    forceContentSize: forceContentSize,
    tableDrawn: tableDrawn,
    displayListSize: displayListSize
  };
})(window.moment, window.TL, window.PAGE);
