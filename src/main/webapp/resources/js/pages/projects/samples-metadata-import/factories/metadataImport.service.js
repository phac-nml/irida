/* global project:true */
/**
 * @file AngularJS Service for handling server interactions for uploading
 * sample metadata.
 */
const $ = require('jquery');

export const sampleMetadataService = $http => {
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
   * @param {string} url Url to put the data to.
   * @param {string} idColumn Name of the column to set as
   * the sample id.
   * @return {object} ajax promise
   */
  const setSampleIdColumn = (url, idColumn) => {
    const data = $.param({sampleIdColumn: idColumn});
    return $http.put(url + '?' + data)
      .then(response => {
        const table = response.data.table;

        // Store it into session storage
        storeProjectData({idColumn, table});

        return response;
      });
  };

  /**
   * Save the metadata back to the samples.
   * @param {string} url the url for saving the metadta.
   * @return {object} ajax promise
   */
  const saveMetadata = url => {
    return $http.put(url);
  };

  return {
    storeProjectData,
    getProjectData,
    setSampleIdColumn,
    saveMetadata
  };
};
