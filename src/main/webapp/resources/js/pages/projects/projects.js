/*exported projectsTable*/
var projectsTable = (function (tl) {
  /**
   * Create a link button to the IRIDA thing
   *
   * @param {Object} data column data
   * @param {String} type type of data
   * @param {Object} full full object for the row.
   * @returns {*}
   */
  function createItemButton(data, type, full) {
    if (tl && tl.BASE_URL) {
      return '<a class="item-link" title="' + data + '" href="' + tl.BASE_URL + 'projects/' + full.identifier + '"><span class="cell-width-200">' + data + '</span></a>';
    } else {
      return data;
    }
  }

  return {
    createItemButton: createItemButton
  };
})(window.TL);

(function ($, tl) {
  var filterBtn = $('#filterProjectsBtn');

  filterBtn.on('click', function () {
    oTable_projectsTable.ajax.reload();
  });

  $('#filterForm').on('submit', function (e) {
    e.preventDefault();
    filterBtn.click();
  });

  $("#organismFilter").select2({
    minimumInputLength: 2,
    ajax: {
      url: tl.BASE_URL +  'projects/ajax/taxonomy/search',
      dataType: 'json',
      data: function (term) {
        return {
          searchTerm: term
        };
      },
      results: function (data) {
        return {results: data};
      }
    }
  });
})(window.jQuery, window.TL);