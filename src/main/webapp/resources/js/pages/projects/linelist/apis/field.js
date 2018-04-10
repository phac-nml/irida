/**
 * Class responsible for ajax call for project sample metadata fields.
 */
import axios from "axios";

class FieldApi {
  /**
   * Get all the MetadataTemplateFields belonging to the templates withing a
   * project.These will be the table headers.
   * @param {number} projectId
   * @returns {Promise}
   */
  static getAllMetadataFields(projectId) {
    return axios({
      method: "get",
      url: `${window.TL.BASE_URL}linelist/fields?projectId=${projectId}`
    });
  }
}

export default FieldApi;
