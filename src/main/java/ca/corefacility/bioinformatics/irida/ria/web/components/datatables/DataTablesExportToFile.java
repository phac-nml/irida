package ca.corefacility.bioinformatics.irida.ria.web.components.datatables;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Used to export datatables to either excel or csv formatted files.
 */
public class DataTablesExportToFile {

	/**
	 * Write data within datatable to an excel formatted file.
	 * 
	 * @param type     {@link DataTablesExportTypes} type of file to create (either excel or csv)
	 * @param response {@link HttpServletResponse}
	 * @param filename {@link String} name of the file to download.
	 * @param models   Data to download in the table
	 * @param headers  for the table
	 * @throws IOException thrown if file cannot be written
	 */
	public static void writeFile(DataTablesExportTypes type, HttpServletResponse response, String filename,
			List<? extends DataTablesExportable> models, List<String> headers) throws IOException {
		if (type.equals(DataTablesExportTypes.excel)) {
			writeToExcel(response, filename, models, headers);
		} else if (type.equals(DataTablesExportTypes.csv)) {
			writeToCSV(response, filename, models, headers);
		} else {
			throw new IllegalArgumentException("Trying to export and unknown table format: " + type);
		}
	}

	/**
	 * Write data within datatable to an excel formatted file.
	 * 
	 * @param response {@link HttpServletResponse}
	 * @param filename {@link String} name of the file to download.
	 * @param models   Data to download in the table
	 * @param headers  for the table
	 * @throws IOException thrown if file cannot be written
	 */
	private static void writeToExcel(HttpServletResponse response, String filename,
			List<? extends DataTablesExportable> models, List<String> headers) throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet();

		// Create the header row
		Row row = sheet.createRow(0);
		int cellNum = 0;
		for (String header : headers) {
			Cell cell = row.createCell(cellNum++);
			cell.setCellValue(header);
		}

		// Add the data to the workbook
		int rowNum = 1;
		for (DataTablesExportable model : models) {
			row = sheet.createRow(rowNum++);
			int cellCount = 0;
			for (String content : model.getExportableTableRow()) {
				Cell cell = row.createCell(cellCount++);
				cell.setCellValue(content);
			}
		}

		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-disposition", "attachment; filename=" + filename + ".xlsx");
		workbook.write(response.getOutputStream());

		workbook.close();
	}

	/**
	 * Write data within datatable to a csv formatted file.
	 * 
	 * @param response {@link HttpServletResponse}
	 * @param filename {@link String} name of the file to download.
	 * @param models   Data to download in the table
	 * @param headers  for the table
	 * @throws IOException thrown if file cannot be written
	 */
	private static void writeToCSV(HttpServletResponse response, String filename,
			List<? extends DataTablesExportable> models, List<String> headers) throws IOException {
		List<String[]> results = new ArrayList<>();
		results.add(headers.toArray(new String[0]));
		for (DataTablesExportable model : models) {
			results.add(model.getExportableTableRow().toArray(new String[0]));
		}

		response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".csv\"");
		response.setContentType("text/csv");
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(response.getOutputStream());
		CSVPrinter printer = CSVFormat.DEFAULT.print(outputStreamWriter);
		printer.printRecords(results);
		printer.flush();
		outputStreamWriter.close();
	}
}
