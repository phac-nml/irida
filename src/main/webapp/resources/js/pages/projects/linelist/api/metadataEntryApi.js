/**
 * Class responsible for ajax call for project sample metadata.
 */
import axios from "axios";

class MetadataEntryApi {
  /**
   * Get all metadata belonging to samples in the current project.
   * These will be the table content
   * @param {number} projectId
   * @returns {Promise}
   */
  static getAllMetadataEntries(projectId) {
    return axios
      .get(`${window.TL.BASE_URL}linelist/entries?projectId=${projectId}`)
      .then(response => response.data);
  }
}

export default MetadataEntryApi;
