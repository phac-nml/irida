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

var datatable = (function(moment, $, tl, page) {
  'use strict';
  // This is used to get a colour for the project label in a table cell
  var projectColourer = new ProjectColourer();


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


  function highlightAssociatedProjectRows(data, type, full) {
    var $div = $("<div><div class='project-label'>&nbsp;" + data + "</div></div>");
    if (full.sampleType === 'ASSOCIATED') {
      var colour = projectColourer.getColour(full.project.label);
      $div.find("div.project-label").css("border-color", colour);
    }
    return $div.html();
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
    formatDate                    : formatDate,
    i18n                          : i18n,
    forceContentSize              : forceContentSize,
    tableDrawn                    : tableDrawn,
    displayListSize               : displayListSize,
    formatSampleLink              : formatSampleLink,
    highlightAssociatedProjectRows: highlightAssociatedProjectRows
  };
})(window.moment, window.jQuery, window.TL, window.PAGE);
