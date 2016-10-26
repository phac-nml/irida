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
  getMetadata(url, template = 'default') {
    return this.$http.get(`${url}/metadata?template=${template}`);
  }

  /**
   * Get the keys associated with the current template
   * @param {string} url current url
   * @param {string} template name of the current template
   * @return {object} ajax promise
   */
  getTemplate(url, template = 'default') {
    return this.$http.get(`${url}/mt?template=${template}`);
  }
}
