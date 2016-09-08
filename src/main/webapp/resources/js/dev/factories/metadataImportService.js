export const sampleMetadataService = () => {
  return {
    setSampleIdColumn: setSampleIdColumn
  };

  /**
   * Set the name of the column that contains the sample id.
   * @param {string} columnName Name of the column to set as
   * the sample id.
   */
  function setSampleIdColumn(columnName) {
    console.log(columnName);
  }
};
