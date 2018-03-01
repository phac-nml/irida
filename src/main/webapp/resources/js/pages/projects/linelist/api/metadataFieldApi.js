/**
 * Service class for MetadataTemplateFields
 */
import axios from "axios";

class MetadataFieldApi {
  /**
   * Get all the MetadataTemplateFields belonging to the templates withing a
   * project.These will be the table headers.
   * @param {number} projectId
   * @returns {Promise}
   */
  static getAllMetadataFields(projectId) {
    return axios
      .get(`${window.TL.BASE_URL}linelist/fields?projectId=${projectId}`)
      .then(response => response.data);
  }
}

export default MetadataFieldApi;
