/**
 * @file AngularJS Service for handling server interactions for uploading
 * sample metadata.
 */

export function sampleMetadataService($http, $window, Upload) {
  // 'project.id' is set in the `project/_bse.html` file
  const URL = $window.location.pathname;

  /**
   * Upload the metadata file to the server for processing.
   * @param {object} file to upload
   * @return {object} ajax promise.
   */
  const uploadMetadata = file => {
    return Upload.upload({
      url: `${URL}/file`,
      data: { file: file }
    });
  };

  /**
   * Get any metadata stored for the current project.
   * @return {object} the metadata stored for this project.
   */
  const getProjectData = () => {
    return $http.get(`${URL}/getMetadata`).then(result => {
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
    return $http({
      method: "POST",
      url: `${URL}/setSampleColumn`,
      data: $.param({ sampleNameColumn }),
      headers: { "Content-Type": "application/x-www-form-urlencoded" }
    }).then(response => response);
  };

  /**
   * Save the metadata back to the samples.
   * @return {object} ajax promise
   */
  const saveMetadata = () => {
    return $http.post(`${URL}/save`);
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
}
