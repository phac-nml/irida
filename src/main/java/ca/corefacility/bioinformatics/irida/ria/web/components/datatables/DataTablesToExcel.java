package ca.corefacility.bioinformatics.irida.ria.web.components.datatables;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.MessageSource;

public class DataTablesToExcel {
	public void writeWorkbook(HttpServletResponse response, String filename, List<? extends DataTablesExportable> models, MessageSource messageSource,
			Locale locale) throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet();

		// Create the header row
		Row row = sheet.createRow(0);
		int cellNum = 0;
		for (String header : models.get(0)
				.getTableHeaders(messageSource, locale)) {
			Cell cell = row.createCell(cellNum++);
			cell.setCellValue(header);
		}

		// Add the data to the workbook
		int rowNum = 1;
		for (int i = 0; i < models.size(); i++) {
			row = sheet.createRow(rowNum++);
			int cellCount = 0;
			for (Object obj : models.get(i)
					.toTableRow()) {
				Cell cell = row.createCell(cellCount++);
				if (obj instanceof Date) {
					cell.setCellValue((Date) obj);
				} else if (obj instanceof String) {
					cell.setCellValue((String) obj);
				} else if (obj instanceof Long) {
					cell.setCellValue((Long) obj);
				}
			}
		}

		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-disposition",
				"attachment; filename=" + filename + ".xlsx");
		workbook.write(response.getOutputStream());
	}
}
