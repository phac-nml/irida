/* global project:true */
/**
 * @file AngularJS Service for handling server interactions for uploading
 * sample metadata.
 */
const $ = require('jquery');

export const sampleMetadataService = $http => {
  const storeHeaders = headers => {
    sessionStorage.setItem("pm-" + project.id, JSON.stringify({headers}));
  };

  /**
   * Get the headers for the current excel file.
   * @return {array} of headers
   */
  const getHeaders = () => {
    const stored = JSON.parse(sessionStorage.getItem("pm-" + project.id));
    if (stored.hasOwnProperty('headers')) {
      return stored.headers;
    }
    return [];
  };

  /**
   * Set the name of the column that contains the sample id.
   * @param {string} url Url to put the data to.
   * @param {string} columnName Name of the column to set as
   * the sample id.
   * @return {object} ajax promise
   */
  const setSampleIdColumn = (url, columnName) => {
    const data = $.param({sampleIdColumn: columnName});
    return $http.put(url + "?" + data)
      .then(response => {
        return response.data;
      });
  };

  return {
    storeHeaders,
    getHeaders,
    setSampleIdColumn
  };
};
