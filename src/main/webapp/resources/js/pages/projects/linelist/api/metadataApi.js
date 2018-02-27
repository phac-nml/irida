/**
 * Class responsible for ajax call for project sample metadata.
 * THE `axios` ajax library is used here as it uses native es6 promises,
 * which will be used later on this page to handle multiple calls simultaneously.
 */
import axios from "axios";

class MetadataApi {
  /**
   * Get all metadata belonging to samples in the current project.
   * These will be the table content
   * @param {number} projectId
   * @returns {Promise<AxiosResponse<any>>}
   */
  static getAllMetadataEntries(projectId) {
    return axios
      .get(`${window.TL.BASE_URL}linelist/entries?projectId=${projectId}`)
      .then(result => result.data);
  }

  /**
   *  Get all the metadata fields belonging to the templates withing a project.
   * These will be the table headers.
   * @param {number} projectId
   * @returns {Promise<AxiosResponse<any>>}
   */
  static getAllMetadataFields(projectId) {
    return axios
      .get(`${window.TL.BASE_URL}linelist/fields?projectId=${projectId}`)
      .then(result => result.data);
  }
}

export default MetadataApi;
