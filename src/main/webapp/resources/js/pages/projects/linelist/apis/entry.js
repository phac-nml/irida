/**
 * Class responsible for ajax call for project sample metadata entries.
 */
import axios from "axios";

class EntryApi {
  /**
   * Get all metadata belonging to samples in the current project.
   * These will be the table content
   * @param {number} projectId
   * @returns {Promise}
   */
  static getAllMetadataEntries(projectId) {
    return axios({
      method: "get",
      url: `${window.TL.BASE_URL}linelist/entries?projectId=${projectId}`
    });
  }
}

export default EntryApi;
