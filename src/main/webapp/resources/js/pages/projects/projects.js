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
      return '<a class="item-link" title="' + data + '" href="' + tl.BASE_URL + full.link + '"><span class="cell-width-200">' + data + '</span></a>';
    } else {
      return data;
    }
  }

  return {
    createItemButton: createItemButton
  };
})(window.TL);
