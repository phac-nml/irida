/*exported projectsTable*/
var projectsTable = (function (tl) {

  function filter(filters) {
    
  }

  /**
   * Create a link button to the IRIDA thing
   *
   * @param {Object} data column data
   * @param {String} type type of data
   * @param {Object} full full object for the row.
   * @returns {*}
   */
  function createItemButton(data, type, full) {
    if (tl && full.link && tl.BASE_URL) {
      return '<a class="item-link btn btn-link btn-xs" title="' + data + '" href="' + tl.BASE_URL + full.link + '"><span class="cell-width-200">' + data + '</span></a>';
    } else {
      return data;
    }
  }

  return {
    createItemButton: createItemButton
  };
})(window.TL);

(function ($) {
  $('#filterProjectsBtn').on('click', function () {
    var nameField = filterForm.name.value;

    if (nameField !== null || nameField !== '') {

    }

  });
})(window.jQuery);
