import XLSX from "xlsx";

/**
 * Create an XLSX or CSV file from data provided.
 * @param {string} filename
 * @param {array} data - array of arrays containing the column data.
 *                       First array should be the column headers
 */
export default ({ filename, data }) => {
  const worksheet = XLSX.utils.aoa_to_sheet(data);
  const workbook = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(workbook, worksheet, "");
  XLSX.writeFile(workbook, filename);
};
