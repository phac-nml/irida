/* global project:true */
/**
 * @file AngularJS Service for handling server interactions for uploading
 * sample metadata.
 */
const $ = require('jquery');

export const sampleMetadataService = ($http, $window) => {
  // 'project.id' is set in the `project/_bse.html` file
  const URL = `${$window.location.pathname}/pm-${project.id}`;

  /**
   * Get any metadata stored for the current project.
   * @return {object} the metadata stored for this project.
   */
  const getProjectData = () => {
    return $http.get(URL)
      .then(result => {
        return result.data;
      });
  };

  /**
   * Set the name of the column that contains the sample id.
   * @param {string} sampleNameColumn Name of the column to set as
   * the sample id.
   * @return {object} ajax promise
   */
  const setSampleIdColumn = sampleNameColumn => {
    const data = $.param({sampleNameColumn});
    return $http.put(`${URL}?${data}`)
      .then(response => response);
  };

  /**
   * Save the metadata back to the samples.
   * @return {object} ajax promise
   */
  const saveMetadata = () => {
    return $http.put(`${URL}/save`);
  };

  const clearProject = () => {
    return $http.get(`${URL}/clear`);
  };

  return {
    getProjectData,
    setSampleIdColumn,
    saveMetadata,
    clearProject
  };
};
