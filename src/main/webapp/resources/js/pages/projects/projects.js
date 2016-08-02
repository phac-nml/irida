/*exported projectsTable*/
/*global oTable_projectsTable */

var projectsTable = (function (tl) {
  /**
   * Create a link button to the IRIDA thing
   *
   * @param {Object} data column data
   * @param {String} type type of data
   * @param {Object} full full object for the row.
   * @returns {String} either a anchor dom element to the project or just the name of the project.
   */
  function createItemButton(data, type, full) {
    if (tl && tl.BASE_URL) {
      var html = '<a class="item-link" title="' + data + '" href="' + tl.BASE_URL + 'projects/' + full.id + '"><span class="cell-width-200">' + data + '</span></a>';
      if(full.remote == true){
        html = html + '&nbsp;<i class="fa fa-exchange" aria-hidden="true"></i>';
      }
      return html;
    } else {
      return data;
    }
  }

  return {
    createItemButton: createItemButton
  };
})(window.TL);

(function ($) {
  $(function () {
    var $filterBtn = $('#filterProjectsBtn');

    $filterBtn.on('click', function () {
      oTable_projectsTable.ajax.reload();
    });

    $('#filterForm').on('keydown', function (e) {
      if (e.which === 13) {
        $filterBtn.click();
      }
    });
  });
})(window.jQuery);