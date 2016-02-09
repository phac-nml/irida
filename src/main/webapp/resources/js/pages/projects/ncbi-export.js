var exportsTable = (function (tl) {
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
      var url = PAGE.URLS.projectBase + full.project.identifier + "/export/" + full.identifier;
      return "<a class='btn btn-link' title='"+data+"' href='" + url + "'>" + data + "</a>";
    }
  }

  function createProjectLink(data, type, full) {
    if(full.identifier) {
      var url = PAGE.URLS.projectBase + full.project.identifier;
      return "<a class='btn btn-link' title='"+data+"' href='" + url + "'>" + full.project.label + "</a>";
    }
  }

  return {
    createLinkButton: createLinkButton,
    createProjectLink: createProjectLink
  };
})(window.TL);
