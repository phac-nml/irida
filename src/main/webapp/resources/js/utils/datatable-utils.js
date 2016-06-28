/*eslint no-undef: 2*/
/*global Option:true*/
/*exported datatable*/

/**
 * Custom function to add colours to project labels.
 */
var ProjectColourer = (function () {
  "use strict";
  
  var index    = 0, // Index of the current colour
      // These colours are intense colours pick from
      // https://www.google.com/design/spec/style/color.html#color-color-palette A better solution might be to allow
      // project managers to set custom colours for projects and store in project settings.
      colours  = ["#D50000", "#C51162", "#AA00FF", "#6200EA", "#304FFE", "#2962FF", "#0091EA", "#00B8D4", "#00BFA5", "#00C853", "#64DD17", "#AEEA00", "#FFD600", "#FFAB00", "#FF6D00"],
      // JSON to store already chosen project colours.
      projects = {};

  function ProjectColours() {
  }

  /**
   * Get a colour for the project name provided
   * @param name Name of the project
   * @returns {string} colour
   */
  ProjectColours.prototype.getColour = function (name) {
    // Check to see if the project already has a colour.
    if (typeof projects[name] === "undefined") {
      // Since currently there is a limited number of colours, make sure you wrap around to the start if
      // all the colours are currently in use.
      if (colours.length === index) {
        index = 0;
      }
      // Select the colour for the project.
      projects[name] = colours[index++];
    }
    return projects[name];
  };
  return ProjectColours;
}());

var RowClickHandler = (function (page) {
  "use strict";
  var selected = [], // List of ids for currently selected items
      lastSelectedId; // Id of the last selected item, for multiple selections

  function RowSelection() {
  }

  function getRowId(row) {
    // Anchor tag expected to have a data attribute 'id'
    // Expected on first anchor in row.
    var anchor = row.getElementsByTagName("a")[0],
        id;
    if (typeof anchor === "undefined") {
      throw new Error("No anchor tag found for sample name");
    }
    id = anchor.dataset.id;
    if(typeof id === "undefined") {
      throw new Error("Expected anchor tag for sample to contain sample identifier");
    }
    return id;
  }

  function selectRow(row) {
    var id = getRowId(row);
    // Check to make sure the row is not already selected.
    // Can occur during multi-select
    if (selected.indexOf(id) === -1) {
      row.classList.add("selected");
      selected.push(id);
    }
  }

  function deselectRow(row) {
    var id = getRowId(row),
        index = selected.indexOf(id);
    // Make sure the row is selected
    if (index > -1) {
      row.classList.remove("selected");
      selected.splice(index, 1);
    }
  }

  function toggleRowCheckbox (row, isSelected) {
    var cb = row.querySelector("input[type=checkbox]");
    cb.checked = isSelected;
  }

  function toggleRowSelection(row) {
    if (row.classList.contains("selected")) {
      deselectRow(row);
      toggleRowCheckbox(row, false);
      lastSelectedId = undefined;
    } else {
      selectRow(row);
      toggleRowCheckbox(row, true);
      lastSelectedId = getRowId(row);
    }
  }

  function selectMultipleRows(currRow) {
    var currRowId = getRowId(currRow),

        // querySelectorAll return a nodeList, this converts it into an array we can use,
        rows      = Array.prototype.slice.call(document.querySelectorAll("tbody tr")),
        ids       = rows.map(function (r) {
          return getRowId(r);
        });

    // Make sure that both ids are present in the list
    var currIndex = ids.indexOf(currRowId),
        prevIndex = ids.indexOf(lastSelectedId);
    if (currIndex === -1 || prevIndex === -1) {
      throw new Error("Last selected row and current selected row are not on this page!");
    }

    // Select current row rows
    selectRow(currRow, currRowId);

    // Select the rows in between
    var min = Math.min(currIndex, prevIndex),
        max = Math.max(currIndex, prevIndex);
    for (; min < max; min++) {
      selectRow(rows[min]);
    }

    // Remove ability to do a multiple select until another row is selected
    lastSelectedId = undefined;

  }

  /**
   * Tell all angular dependencies that the selected count has been updated;
   * @param count
   */
  function broadcastSelectionCounts(count) {
    var elm = document.querySelector("[ng-app]");
    var scope = angular.element(elm).scope();
    scope.$broadcast("SAMPLE_SELECTION_EVENT", {count: count});
  }

  /**
   * Update the table selected counts in the information area.
   * @param count
   */
  function displaySelectionCounts(count) {
    var selectDiv = document.querySelector(".selected-counts");
    if(selected.length === 0 ) {
      selectDiv.innerHTML = page.i18n.selectedCounts.none;
    } else if (selected.length === 1) {
      selectDiv.innerHTML = page.i18n.selectedCounts.one;
    } else {
      selectDiv.innerHTML = page.i18n.selectedCounts.other.replace("{count}", count);
    }
  }

  /**
   * Complete all updates when the count changes.
   * @param count
   */
  function updateSelectionCounts(count) {
    displaySelectionCounts(count);
    broadcastSelectionCounts(count);
  }

  /**
   * Table body click event
   * @param row
   * @param isMultiple
   */
  RowSelection.prototype.clickEvent = function (row, isMultiple) {

    // To be a true multi select there needs to be a sample selected already,
    // and the shift key needs to be pressed.
    if (lastSelectedId !== undefined && isMultiple) {
      selectMultipleRows(row);
    } else {
      toggleRowSelection(row);
    }

    // Update the selected counts
    updateSelectionCounts(selected.length);
  };

  /**
   * Check to see an item is selected.  This is used when the table page has changed.
   * @param id
   * @returns {boolean}
   */
  RowSelection.prototype.isRowSelected = function (id) {
    return selected.indexOf(id) > -1;
  };

  /**
   * Get List of all selected ids.
   * @returns {Array}
   */RowSelection.prototype.getSelectedIds = function () {
    return selected;
  };

  /**
   * Clear all selected indexes
   */
  RowSelection.prototype.clearSelected = function () {
    // Need to clear the class from the table:
    var rows = document.querySelectorAll(".selected");
    [].forEach.call(rows, function (row) {
      deselectRow(row);
    });
    selected = [];
    updateSelectionCounts(0);
  };

  return RowSelection;
}(window.PAGE));

var datatable = (function(moment, tl, page) {
  'use strict';
  // This is used to get a colour for the project label in a table cell
  var projectColourer = new ProjectColourer(),
      rowClickHandler = new RowClickHandler();



  function _getDate(date, type) {
    return moment(date).format(tl.date.moment[type]);
  }

  function _createDateElement(date, format) {
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
    var dateNode = document.createTextNode(_getDate(date, format));
    dateSpan.appendChild(dateNode);
    return wrapper.innerHTML;
  }

  /**
   * Format the date in a table.
   * Requires the page to have the date format object.
   * @param date Date from server.
   * @returns {*}
   */
  function formatDate(date) {
    if (moment !== undefined && date !== undefined && tl.date && tl.date.moment.short) {
      return _createDateElement(date, "short");
    } else {
      return new Date(date);
    }
  }

  function formatSampleLink(data, type, full) {
    if(typeof full.sample === 'undefined'){
      return data;
    }
    else {
      return "<a data-id='" + full.sample.identifier + "' class='btn btn-link sample-label' href='" + tl.BASE_URL + "projects/" + full.project.identifier + "/samples/" + full.sample.identifier + "'>" + data + "</a>";
    }
  }

  function formatDateWithTime(date) {
    if (moment !== undefined && date !== undefined && tl.date && tl.date.moment.short) {
      return _createDateElement(date, "full");
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


  function highlightAssociatedProjectRows(data, type, full) {
    var outer = document.createElement("div");
    var div = document.createElement("div");
    div.classList.add("project-label");
    var text = document.createTextNode(data);
    div.appendChild(text);

    if (full.sampleType === 'ASSOCIATED') {
      var colour = projectColourer.getColour(full.project.label);
      div.style.borderColor = colour;
    }

    outer.appendChild(div);
    return outer.innerHTML;
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

  function projectRowCreated(row, item) {
    if (rowClickHandler.isRowSelected(item.sample.identifier)) {
      row.classList.add("selected");
      row.querySelector("input[type=checkbox]").checked = true;
    }
  }

  /**
   * Handler for clicking the table body.
   * @param event - MouseClick Event
   */
  function tbodyClickEvent(event) {
    // Recursive function to find the tr that was clicked.
    function findRow(el) {
      if (el.nodeName === "TR") {
        return el;
      }
      return findRow(el.parentNode);
    }

    // If they click the link, don't bother highlighting the row, it just looks bad.
    if (typeof event.target.href === "undefined") {
      rowClickHandler.clickEvent(findRow(event.target), event.shiftKey);
    }
  }

  function getSelectedIds() {
    return rowClickHandler.getSelectedIds();
  }

  function clearSelected() {
    rowClickHandler.clearSelected();
  }

  function formatCheckbox(row, item, data) {
    var wrapper = document.createElement("div");
    var cb = document.createElement("input");
    wrapper.appendChild(cb);
    cb.type = "checkbox";

    return wrapper.innerHTML;
  }

  return {
    formatDate                    : formatDate,
    formatDateWithTime            : formatDateWithTime,
    i18n                          : i18n,
    forceContentSize              : forceContentSize,
    tableDrawn                    : tableDrawn,
    displayListSize               : displayListSize,
    formatSampleLink              : formatSampleLink,
    highlightAssociatedProjectRows: highlightAssociatedProjectRows,
    projectRowCreated             : projectRowCreated,
    tbodyClickEvent               : tbodyClickEvent,
    getSelectedIds                : getSelectedIds,
    clearSelected                 : clearSelected,
    formatCheckbox                : formatCheckbox
  };
})(window.moment, window.TL, window.PAGE);
