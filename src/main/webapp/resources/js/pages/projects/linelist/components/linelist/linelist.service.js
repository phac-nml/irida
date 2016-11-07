/**
 * Service class for Projects Linelist.
 */
export class LinelistService {
  constructor($http) {
    this.$http = $http;
  }

  /**
   * Get the metadata associated with the current template
   * @param {string} url current url
   * @param {string} template name of the current template
   * @return {object} ajax promise
   */
  getMetadata(url, template) {
    let metadataUrl = `${url}/metadata`;
    if (template) {
      metadataUrl = `${metadataUrl}?templateId=${template}`;
    }
    return this.$http.get(metadataUrl)
      .then(result => result.data.metadata);
  }

  /**
   * Get the keys associated with the current template
   * @param {string} url current url
   * @param {number} template id of the current template
   * @return {object} ajax promise
   */
  getTemplate(url, template) {
    let templateUrl = `${url}/fields`;
    if (template) {
      templateUrl = `${templateUrl}?templateId=${template}`;
    }
    return this.$http.get(templateUrl)
      .then(result => result.data.fields);
  }
}
