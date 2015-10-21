/*exported projectsTable*/
var projectsTable = (function(tl) {

  /**
   * Create a link button to the IRIDA thing
   *
   * @param {Object} data column data
   * @param {string} type type of data
   * @param {Object} full full object for the row.
   * @returns {*}
   */
  function createItemButton(data, type, full) {
    if (tl && full.link && tl.BASE_URL) {
      return '<a class="item-link btn btn-default table-cell-override btn-xs" title="' + data + '" href="' + tl.BASE_URL + full.link + '">' + data + '</a>';
    } else {
      return data;
    }
  }

  return {
    createItemButton: createItemButton
  };
})(window.TL);
