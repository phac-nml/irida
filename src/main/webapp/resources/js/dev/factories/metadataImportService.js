const $ = require('jquery');

export const sampleMetadataService = $http => {
  return {
    setSampleIdColumn: setSampleIdColumn
  };

  /**
   * Set the name of the column that contains the sample id.
   * @param {string} url Url to put the data to.
   * @param {string} columnName Name of the column to set as
   * the sample id.
   * @return {object} ajax promise
   */
  function setSampleIdColumn(url, columnName) {
    const data = $.param({sampleIdColumn: columnName});
    return $http.put(url + "?" + data)
      .then(response => {
        return response.data;
      });
  }
};
