package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.MetadataImportFileTypeNotSupportedError;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.ria.utilities.SampleMetadataStorage;
import ca.corefacility.bioinformatics.irida.ria.utilities.SampleMetadataStorageRow;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.base.Strings;

/**
 * UI service to handle parsing metadata files so they can be saved to the
 * session.
 */
@Component
public class UIMetadataFileImportService {

	private static final Logger logger = LoggerFactory.getLogger(UIMetadataFileImportService.class);

	private final ProjectService projectService;
	private final SampleService sampleService;

	@Autowired
	public UIMetadataFileImportService(ProjectService projectService, SampleService sampleService) {
		this.projectService = projectService;
		this.sampleService = sampleService;
	}

	/**
	 * Parse metadata from an csv file.
	 *
	 * @param projectId
	 *            {@link Long} The project identifier.
	 * @param inputStream
	 *            The inputStream of the csv file.
	 * @return {@link SampleMetadataStorage} contains the metadata from file.
	 * @throws IOException
	 *             thrown if the extension does not exist.
	 */
	public SampleMetadataStorage parseCSV(Long projectId, InputStream inputStream) throws IOException {
		SampleMetadataStorage storage = new SampleMetadataStorage();

		CSVParser parser = CSVParser.parse(inputStream, StandardCharsets.UTF_8,
				CSVFormat.RFC4180.withFirstRecordAsHeader().withTrim().withIgnoreEmptyLines());
		List<SampleMetadataStorageRow> rows = new ArrayList<>();

		// save headers
		Map<String, Integer> headersSet = parser.getHeaderMap();
		List<String> headersList = new ArrayList<>(headersSet.keySet());
		storage.setHeaders(headersList);

		// save data
		for (CSVRecord row : parser) {
			Map<String, String> rowMap = new HashMap<>();
			for (String key : row.toMap().keySet()) {
				String value = row.toMap().get(key);
				rowMap.put(key, value);
			}
			rows.add(new SampleMetadataStorageRow(rowMap));
		}
		storage.setRows(rows);
		storage.setSampleNameColumn(findColumnName(projectId, rows));
		parser.close();

		return storage;
	}

	/**
	 * Parse metadata from an excel file.
	 *
	 * @param projectId
	 *            {@link Long} The project identifier.
	 * @param inputStream
	 *            The inputStream of the excel file.
	 * @param extension
	 *            The extension of the excel file.
	 * @return {@link SampleMetadataStorage} contains the metadata from file.
	 * @throws IOException
	 *             thrown if the extension does not exist.
	 */
	public SampleMetadataStorage parseExcel(Long projectId, InputStream inputStream, String extension)
			throws IOException {
		SampleMetadataStorage storage = new SampleMetadataStorage();
		Workbook workbook = null;

		// Check the type of workbook
		switch (extension) {
		case "xlsx":
			workbook = new XSSFWorkbook(inputStream);
			break;
		case "xls":
			workbook = new HSSFWorkbook(inputStream);
			break;
		default:
			// Should never reach here as the uploader limits to .csv, .xlsx and
			// .xlx files.
			throw new MetadataImportFileTypeNotSupportedError(extension);
		}

		// Only look at the first sheet in the workbook as this should be the
		// file we want.
		Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.iterator();

		List<String> headers = getWorkbookHeaders(rowIterator.next());
		storage.setHeaders(headers);

		// Get the metadata out of the table.
		List<SampleMetadataStorageRow> rows = new ArrayList<>();
		while (rowIterator.hasNext()) {
			Map<String, String> rowMap = new HashMap<>();
			Row row = rowIterator.next();
			Iterator<Cell> cellIterator = row.cellIterator();
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();

				int columnIndex = cell.getColumnIndex();
				if (columnIndex < headers.size()) {
					String header = headers.get(columnIndex);

					if (!Strings.isNullOrEmpty(header)) {
						// Need to ignore empty headers.
						if (cell.getCellType().equals(CellType.NUMERIC)) {
							/*
							 * This is a special handler for number cells. It
							 * was requested that numbers keep their formatting
							 * from their excel files. E.g. 2.222222 with
							 * formatting for 2 decimal places will be saved as
							 * 2.22.
							 */
							DataFormatter formatter = new DataFormatter();
							String value = formatter.formatCellValue(cell);
							rowMap.put(header, value);
						} else {
							rowMap.put(header, cell.getStringCellValue());
						}
					}
				}
			}
			rows.add(new SampleMetadataStorageRow(rowMap));
		}
		storage.setRows(rows);
		storage.setSampleNameColumn(findColumnName(projectId, rows));

		if (extension.equals("xlsx")) {
			workbook.close();
		}

		return storage;
	}

	/**
	 * Extract the headers from an excel file.
	 *
	 * @param row
	 *            {@link Row} First row from the excel file.
	 * @return {@link List} of {@link String} header values.
	 */
	private List<String> getWorkbookHeaders(Row row) {
		// We want to return a list of the table headers back to the UI.
		List<String> headers = new ArrayList<>();

		// Get the column headers
		Iterator<Cell> headerIterator = row.cellIterator();
		while (headerIterator.hasNext()) {
			Cell headerCell = headerIterator.next();
			CellType cellType = headerCell.getCellType();

			String headerValue;
			if (cellType.equals(CellType.STRING)) {
				headerValue = headerCell.getStringCellValue().trim();
			} else {
				headerValue = String.valueOf(headerCell.getNumericCellValue()).trim();
			}

			// Leave empty headers for now, we will remove those columns later.
			headers.add(headerValue);
		}
		return headers;
	}

	/**
	 * Find the sample name column, given the rows of a file.
	 *
	 * @param projectId
	 *            {@link Long} The project identifier.
	 * @param rows
	 *            {@link Row} The rows from the excel file.
	 * @return {@link String} column name.
	 */
	private String findColumnName(Long projectId, List<SampleMetadataStorageRow> rows) {
		String columnName = null;
		int col = 0;
		int numRows = rows.size();

		while (columnName == null && col < numRows) {
			columnName = findColumnNameInRow(projectId, rows.get(col));
			col++;
		}

		return columnName;
	}

	/**
	 * Find the sample name column, given a row of a file.
	 *
	 * @param projectId
	 *            {@link Long} The project identifier.
	 * @param row
	 *            {@link Row} A row from the excel file.
	 * @return {@link String} column name.
	 */
	private String findColumnNameInRow(Long projectId, SampleMetadataStorageRow row) {
		String columnName = null;
		Project project = projectService.read(projectId);
		Iterator<Map.Entry<String, String>> iterator = row.getEntry().entrySet().iterator();

		while (iterator.hasNext() && columnName == null) {
			String key = null;
			String value = null;
			try {
				Map.Entry<String, String> entry = iterator.next();
				key = entry.getKey();
				value = entry.getValue();

				if (sampleService.getSampleBySampleName(project, value) != null) {
					columnName = key;
				}
			} catch (EntityNotFoundException entityNotFoundException) {
				logger.trace("Sample " + value + " in project " + project.getId() + " is not found.",
						entityNotFoundException);
			}
		}

		return columnName;
	}
}