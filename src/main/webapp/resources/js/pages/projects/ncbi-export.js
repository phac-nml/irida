/*exported exportsTable*/
var exportsTable = (function (page) {
  /**
   * Create a link button to the export thing
   *
   * @param {Object} data column data
   * @param {String} type type of data
   * @param {Object} full full object for the row.
   * @returns {String} either a anchor dom element to the project or just the name of the project.
   */
  function createLinkButton(data, type, full) {
    if(full.identifier) {
      return "<a class='btn btn-link' title='"+data+"' href='" + page.URLS.exportBase + full.identifier + "'>" + data + "</a>";
    }
  }

  return {
    createLinkButton: createLinkButton
  };
})(window.PAGE);
