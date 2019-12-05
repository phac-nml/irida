import XLSX from "xlsx";

export default ({ filename, data }) => {
  const worksheet = XLSX.utils.aoa_to_sheet(data);
  const workbook = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(workbook, worksheet, "");
  XLSX.writeFile(workbook, filename);
};
