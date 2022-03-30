import * as XLSX from "xlsx";

/**
 * Create an XLSX file from data provided.
 * @param {string} filename
 * @param {string} data - csv representation of the table data.
 */
export default ({ filename, data }) => {
  const workbook = XLSX.read(data, { type: 'binary', raw: true, dense: true });
  XLSX.writeFile(workbook, filename, { bookType: 'xlsx', type: 'base64' });
};
