/**
 * @file AngularJS Service for handling server interactions for uploading
 * sample metadata.
 */
const $ = require('jquery');

export const sampleMetadataService = ($http, $window, Upload) => {
  // 'project.id' is set in the `project/_bse.html` file
  const URL = $window.location.pathname;

  /**
   * Upload the metadata file to the server for processing.
   * @param {object} file to upload
   * @return {object} ajax promise.
   */
  const uploadMetadata = file => {
    return Upload
      .upload({
        url: `${URL}/uploadFile`,
        data: {file: file}
      });
  };

  /**
   * Get any metadata stored for the current project.
   * @return {object} the metadata stored for this project.
   */
  const getProjectData = () => {
    return $http.get(`${URL}/getMetadata`)
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
    return $http.put(`${URL}/setSampleColumn?${data}`)
      .then(response => response);
  };

  /**
   * Save the metadata back to the samples.
   * @return {object} ajax promise
   */
  const saveMetadata = () => {
    return $http.put(`${URL}/save`);
  };

  /**
   * Clear any metadata uploaded to the server.
   * @return {object} ajax promise
   */
  const clearProject = () => {
    return $http.get(`${URL}/clear`);
  };

  return {
    uploadMetadata,
    getProjectData,
    setSampleIdColumn,
    saveMetadata,
    clearProject
  };
};
