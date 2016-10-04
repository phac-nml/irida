/* global project:true */
/**
 * @file AngularJS Service for handling server interactions for uploading
 * sample metadata.
 */
const $ = require('jquery');

export const sampleMetadataService = ($http, $window) => {
  // 'project.id' is set in the `project/_bse.html` file
  const PROJECT_KEY = `pm-${project.id}`;

  /**
   * Get any metadata stored for the current project.
   * @return {object} the metadata stored for this project.
   */
  const getProjectData = () => {
    const stored = sessionStorage.getItem(PROJECT_KEY);
    if (stored === null) {
      return {};
    }
    return JSON.parse(stored);
  };

  /**
   * Store the new data into the session.
   * @param {object} data the data to store into the session.
   */
  const storeProjectData = data => {
    const json = getProjectData();
    Object.assign(json, data);
    sessionStorage.setItem(PROJECT_KEY, JSON.stringify(json));
  };

  /**
   * Set the name of the column that contains the sample id.
   * @param {string} idColumn Name of the column to set as
   * the sample id.
   * @return {object} ajax promise
   */
  const setSampleIdColumn = idColumn => {
    const data = $.param({sampleIdColumn: idColumn});
    return $http.put($window.location.pathname + '?' + data)
      .then(response => {
        const goodRows = response.data.goodRows;
        const badRows = response.data.badRows;

        const tables = {goodRows, badRows};

        // Store it into session storage
        storeProjectData({idColumn, tables});

        return response;
      });
  };

  /**
   * Save the metadata back to the samples.
   * @return {object} ajax promise
   */
  const saveMetadata = () => {
    return $http.put($window.location.pathname + '/save');
  };

  const clearProject = () => {
    sessionStorage.clear();
  };

  return {
    storeProjectData,
    getProjectData,
    setSampleIdColumn,
    saveMetadata,
    clearProject
  };
};
