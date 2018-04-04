/**
 * Service class for MetadataTemplateFields
 */
import $ from "jquery";

class MetadataFieldApi {
  /**
   * Get all the MetadataTemplateFields belonging to the templates withing a
   * project.These will be the table headers.
   * @param {number} projectId
   * @returns {Promise}
   */
  static getAllMetadataFields(projectId) {
    return $.get(`${window.TL.BASE_URL}linelist/fields?projectId=${projectId}`);
  }
}

export default MetadataFieldApi;
