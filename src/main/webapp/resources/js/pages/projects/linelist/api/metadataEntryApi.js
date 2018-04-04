/**
 * Class responsible for ajax call for project sample metadata.
 */
import $ from "jquery";

class MetadataEntryApi {
  /**
   * Get all metadata belonging to samples in the current project.
   * These will be the table content
   * @param {number} projectId
   * @returns {Promise}
   */
  static getAllMetadataEntries(projectId) {
    return $.get(
      `${window.TL.BASE_URL}linelist/entries?projectId=${projectId}`
    );
  }
}

export default MetadataEntryApi;
