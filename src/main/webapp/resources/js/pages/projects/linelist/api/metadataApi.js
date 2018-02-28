/**
 * Class responsible for ajax call for project sample metadata.
 * The `axios` ajax library is used since it allows for the native es6 promises,
 * which will be used later on this page to handle multiple calls simultaneously.
 */
import axios from "axios";

class MetadataApi {
  /**
   *
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

  /**
   *  Get all the metadata fields belonging to the templates withing a project.
   * These will be the table headers.
   * @param {number} projectId
   * @returns {Promise}
   */
  static getAllMetadataFields(projectId) {
    return axios
      .get(`${window.TL.BASE_URL}linelist/fields?projectId=${projectId}`)
      .then(response => response.data);
  }
}

export default MetadataApi;
