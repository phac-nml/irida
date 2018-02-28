/**
 * Class responsible for ajax call for project sample metadata.
 * THE `cross-fetch` to polyfill the natural fetch allowing for native es6 promises,
 * which will be used later on this page to handle multiple calls simultaneously.
 */
import fetch from "cross-fetch";

class MetadataApi {
  /**
   * Get all metadata belonging to samples in the current project.
   * These will be the table content
   * @param {number} projectId
   * @returns {Promise<AxiosResponse<any>>}
   */
  static getAllMetadataEntries(projectId) {
    return fetch(
      `${window.TL.BASE_URL}linelist/entries?projectId=${projectId}`
    ).then(response => response.json());
  }

  /**
   *  Get all the metadata fields belonging to the templates withing a project.
   * These will be the table headers.
   * @param {number} projectId
   * @returns {Promise<AxiosResponse<any>>}
   */
  static getAllMetadataFields(projectId) {
    return fetch(
      `${window.TL.BASE_URL}linelist/fields?projectId=${projectId}`
    ).then(response => response.json());
  }
}

export default MetadataApi;
