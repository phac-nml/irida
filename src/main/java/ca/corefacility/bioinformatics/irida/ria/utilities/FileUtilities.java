package ca.corefacility.bioinformatics.irida.ria.utilities;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.*;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.ProjectSampleAnalysisOutputInfo;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.ria.web.dto.ExcelCol;
import ca.corefacility.bioinformatics.irida.ria.web.dto.ExcelData;
import ca.corefacility.bioinformatics.irida.ria.web.dto.ExcelHeader;
import ca.corefacility.bioinformatics.irida.ria.web.dto.ExcelRow;

import com.github.pjfanning.xlsx.StreamingReader;
import com.github.pjfanning.xlsx.impl.StreamingCell;

/**
 * Download a zip archive of all output files within an {@link AnalysisSubmission}
 */
public class FileUtilities {
	private static final Logger logger = LoggerFactory.getLogger(FileUtilities.class);
	public static final String CONTENT_DISPOSITION = "Content-Disposition";
	public static final String ATTACHMENT_FILENAME = "attachment;filename=";
	public static final String CONTENT_TYPE_APPLICATION_ZIP = "application/zip";
	public static final String CONTENT_TYPE_TEXT = "text/plain";
	public static final String EXTENSION_ZIP = ".zip";
	public static final String EXTENSION_HTML_ZIP = ".html.zip";
	private static final Pattern regexExt = Pattern.compile("^.*\\.(\\w+)$");

	/**
	 * Utility method for download a zip file containing all output files from an analysis.
	 *
	 * @param response {@link HttpServletResponse}
	 * @param fileName Name fo the file to create
	 * @param files    Set of {@link AnalysisOutputFile}
	 */
	public static void createAnalysisOutputFileZippedResponse(HttpServletResponse response, String fileName,
			Set<AnalysisOutputFile> files) {
		/*
		 * Replacing spaces and commas as they cause issues with
		 * Content-disposition response header.
		 */
		fileName = formatName(fileName);

		logger.debug("Creating zipped file response. [" + fileName + "]");

		// set the response headers before we do *ANYTHING* so that the filename
		// actually appears in the download dialog
		response.setHeader(CONTENT_DISPOSITION, ATTACHMENT_FILENAME + fileName + EXTENSION_ZIP);
		// for zip file
		response.setContentType(CONTENT_TYPE_APPLICATION_ZIP);

		try (ServletOutputStream responseStream = response.getOutputStream();
				ZipOutputStream outputStream = new ZipOutputStream(responseStream)) {

			for (AnalysisOutputFile file : files) {
				if (!Files.exists(file.getFile())) {
					response.setStatus(404);
					throw new FileNotFoundException();
				}
				// 1) Build a folder/file name
				StringBuilder zipEntryName = new StringBuilder(fileName);
				zipEntryName.append("/").append(file.getLabel());

				// 2) Tell the zip stream that we are starting a new entry in
				// the archive.
				outputStream.putNextEntry(new ZipEntry(zipEntryName.toString()));

				// 3) COPY all of thy bytes from the file to the output stream.
				Files.copy(file.getFile(), outputStream);

				// 4) Close the current entry in the archive in preparation for
				// the next entry.
				outputStream.closeEntry();

				ObjectMapper objectMapper = new ObjectMapper();
				byte[] bytes = objectMapper.writeValueAsBytes(file);
				outputStream.putNextEntry(new ZipEntry(zipEntryName.toString() + "-prov.json"));
				outputStream.write(bytes);
				outputStream.closeEntry();
			}

			// Tell the output stream that you are finished downloading.
			outputStream.finish();
			outputStream.close();
		} catch (IOException e) {
			// this generally means that the user has cancelled the download
			// from their web browser; we can safely ignore this
			logger.debug("This *probably* means that the user cancelled the download, "
					+ "but it might be something else, see the stack trace below for more information.", e);
		} catch (Exception e) {
			logger.error("Download failed...", e);
		}
	}

	/**
	 * Utility method for download a zip file containing all output files from an analysis.
	 * 
	 * @param response {@link HttpServletResponse}
	 * @param fileName Name fo the file to create
	 * @param files    Set of {@link AnalysisOutputFile}
	 */
	public static void createBatchAnalysisOutputFileZippedResponse(HttpServletResponse response, String fileName,
			Map<ProjectSampleAnalysisOutputInfo, AnalysisOutputFile> files) {
		/*
		 * Replacing spaces and commas as they cause issues with
		 * Content-disposition response header.
		 */
		fileName = formatName(fileName);
		logger.debug(
				"Creating zipped file response. [" + fileName + "] with " + files.size() + " analysis output files.");

		// set the response headers before we do *ANYTHING* so that the filename
		// actually appears in the download dialog for zip file
		response.setHeader(CONTENT_DISPOSITION, ATTACHMENT_FILENAME + fileName + EXTENSION_ZIP);
		response.setContentType(CONTENT_TYPE_APPLICATION_ZIP);

		try (ServletOutputStream responseStream = response.getOutputStream();
				ZipOutputStream outputStream = new ZipOutputStream(responseStream)) {
			for (Map.Entry<ProjectSampleAnalysisOutputInfo, AnalysisOutputFile> entry : files.entrySet()) {
				final AnalysisOutputFile file = entry.getValue();
				final ProjectSampleAnalysisOutputInfo outputInfo = entry.getKey();
				if (!Files.exists(file.getFile())) {
					response.setStatus(404);
					throw new FileNotFoundException(
							"File '" + file.getFile().toFile().getAbsolutePath() + "' does not exist!");
				}
				// 1) Build a folder/file name
				// trying to pack as much useful info into the filename as possible!
				String outputFilename = getUniqueFilename(file.getFile(), outputInfo.getSampleName(),
						outputInfo.getSampleId(), +outputInfo.getAnalysisSubmissionId());
				// 2) Tell the zip stream that we are starting a new entry in
				// the archive.
				outputStream.putNextEntry(new ZipEntry(fileName + "/" + outputFilename));

				// 3) COPY all of thy bytes from the file to the output stream.
				Files.copy(file.getFile(), outputStream);

				// 4) Close the current entry in the archive in preparation for
				// the next entry.
				outputStream.closeEntry();
			}

			// Tell the output stream that you are finished downloading.
			outputStream.finish();
			outputStream.close();
		} catch (IOException e) {
			// this generally means that the user has cancelled the download
			// from their web browser; we can safely ignore this
			logger.debug("This *probably* means that the user cancelled the download, "
					+ "but it might be something else, see the stack trace below for more information.", e);
		} catch (Exception e) {
			logger.error("Download failed...", e);
		}
	}

	/**
	 * Utility method for download single file from an analysis.
	 *
	 * @param response {@link HttpServletResponse}
	 * @param file     Set of {@link AnalysisOutputFile}
	 * @param fileName Filename
	 */
	public static void createSingleFileResponse(HttpServletResponse response, AnalysisOutputFile file,
			String fileName) {
		fileName = formatName(fileName);

		// set the response headers before we do *ANYTHING* so that the filename
		// actually appears in the download dialog
		response.setHeader(CONTENT_DISPOSITION, ATTACHMENT_FILENAME + fileName);
		response.setContentType(CONTENT_TYPE_TEXT);

		try (ServletOutputStream outputStream = response.getOutputStream()) {
			Files.copy(file.getFile(), response.getOutputStream());
		} catch (IOException e) {
			// this generally means that the user has cancelled the download
			// from their web browser; we can safely ignore this
			logger.debug("This *probably* means that the user cancelled the download, "
					+ "but it might be something else, see the stack trace below for more information.", e);
		} catch (Exception e) {
			logger.error("Download failed...", e);
		}
	}

	/**
	 * Utility method for download single file from an analysis.
	 *
	 * @param response {@link HttpServletResponse}
	 * @param file     Set of {@link AnalysisOutputFile}
	 */
	public static void createSingleFileResponse(HttpServletResponse response, AnalysisOutputFile file) {
		String fileName = file.getLabel();
		FileUtilities.createSingleFileResponse(response, file, fileName);
	}

	/**
	 * Method to remove unwanted characters from the filename.
	 *
	 * @param name Name of the file
	 * @return The name with unwanted characters removed.
	 */
	private static String formatName(String name) {
		return name.replaceAll("\\s", "_").replaceAll(",", "");
	}

	/**
	 * Get file extension from filepath.
	 * <p>
	 * Uses simple regex to parse file extension {@code ^.*\.(\w+)$}.
	 *
	 * @param filepath The {@link Path} of a file to retrieve ext.
	 * @return File extension if found; otherwise empty string
	 */
	public static String getFileExt(Path filepath) {
		Matcher matcher = regexExt.matcher(filepath.getFileName().toString());
		String ext = "";
		if (matcher.matches()) {
			ext = matcher.group(1).toLowerCase();
		}
		if (ext.equals("html")) {
			try {
				if (isZippedFile(filepath)) {
					ext = "html-zip";
				}
			} catch (IOException e) {
				logger.error("Could not find file " + filepath.toString());
			}
		}
		if (ext.equals("zip") && filepath.getFileName().toString().endsWith(EXTENSION_HTML_ZIP)) {
			ext = "html-zip";
		}

		return ext;
	}

	/**
	 * Get a unique filename for a filePath from an analysis output file.
	 *
	 * @param filePath             The {@link Path} of the file to generate a unique filename for
	 * @param sampleName           The name of the {@link Sample} associated with the analysis
	 * @param sampleId             The id of the {@link Sample} associated with the analysis
	 * @param analysisSubmissionId The id of the {@link AnalysisSubmission} that generated the file
	 * @return Unique filename of the format
	 *         SAMPLENAME-sampleId-SAMPLEID-analysisSubmissionId-ANALYSISSUBMISSIONID-ORIGFILENAME
	 */
	public static String getUniqueFilename(Path filePath, String sampleName, Long sampleId, Long analysisSubmissionId) {
		String ext = getFileExt(filePath);
		String filename = filePath.getFileName().toString();

		String prefix = sampleName + "-sampleId-" + sampleId + "-analysisSubmissionId-" + analysisSubmissionId + "-";
		filename = prefix + filename;
		if (ext.equals("html-zip") && !filename.endsWith(EXTENSION_HTML_ZIP)) {
			filename = filename + ".zip";
		}
		logger.debug("File Name: " + filename);

		return filename;
	}

	/**
	 * Read bytes of length {@code chunk} of a file starting at byte {@code seek}.
	 *
	 * @param raf   File reader
	 * @param seek  FilePointer position to start reading at
	 * @param chunk Number of bytes to read from file
	 * @return Chunk of file as String
	 * @throws IOException if error enountered while reading file
	 */
	public static String readChunk(RandomAccessFile raf, Long seek, Long chunk) throws IOException {
		raf.seek(seek);
		byte[] bytes = new byte[Math.toIntExact(chunk)];
		final int bytesRead = raf.read(bytes);
		if (bytesRead == -1) {
			return "";
		}
		return new String(bytes, 0, bytesRead, Charset.defaultCharset());
	}

	/**
	 * Read a specified number of lines from a file.
	 *
	 * @param reader File reader
	 * @param limit  Limit to the number of lines to read
	 * @param start  Optional line number to start reading at
	 * @param end    Optional line number to read up to
	 * @return Lines read from file
	 */
	public static List<String> readLinesLimit(BufferedReader reader, Long limit, Long start, Long end) {
		Long linesLimit = (limit != null) ? limit : 100L;
		start = (start == null) ? 0 : start;
		if (end != null && end > start) {
			linesLimit = end - start + 1;
		}
		return reader.lines().skip(start == 0 ? 1L : start).limit(linesLimit).collect(Collectors.toList());
	}

	/**
	 * Read lines from file using a {@link RandomAccessFile}.
	 * <p>
	 * Use this method if preserving the {@link RandomAccessFile#getFilePointer()} for continuing reading is important.
	 * For most use cases, {@link FileUtilities#readLinesLimit(BufferedReader, Long, Long, Long)} will perform better
	 * due to bufffered reading.
	 *
	 * @param randomAccessFile File reader
	 * @param limit            Limit to the number of lines to read
	 * @return Lines read from file
	 * @throws IOException if error enountered while reading file
	 */
	public static List<String> readLinesFromFilePointer(RandomAccessFile randomAccessFile, Long limit)
			throws IOException {
		ArrayList<String> lines = new ArrayList<>();
		String line;
		while (lines.size() < limit && (line = randomAccessFile.readLine()) != null) {
			lines.add(line);
		}
		return lines;
	}

	/**
	 * Parse the data from an {@link AnalysisOutputFile} excel file.
	 *
	 * @param outputFile {@link AnalysisOutputFile} The excel file to parse
	 * @param sheetIndex The index of the sheet to parse
	 * @return parsed excel file data
	 */
	public static ExcelData parseExcelFile(AnalysisOutputFile outputFile, int sheetIndex) {
		try {
			InputStream is = new FileInputStream(new File(outputFile.getFile().toAbsolutePath().toString()));
			Workbook workbook = StreamingReader.builder().open(is);

			Sheet sheet = workbook.getSheetAt(sheetIndex);
			Iterator<Row> rowIterator = sheet.iterator();
			List<String> excelSheetNames = getExcelSheetNames(workbook);
			List<ExcelHeader> headers = getWorkbookHeaders(rowIterator.next());
			List<ExcelRow> excelRows = new ArrayList<>();

			for (Row row : sheet) {
				List<ExcelCol> excelCols = new ArrayList<>();
				boolean hasRowData = false;

				for (int i = 0; i < headers.size(); i++) {
					Cell cell = row.getCell(i);
					// Since the iterators skip over blank columns
					// we add a a null column in place if the column
					// is blank
					if (cell == null) {
						cell = new StreamingCell(sheet, i, row.getRowNum(), false);
					}
					CellType cellType = cell.getCellType();
					int columnIndex = cell.getColumnIndex();
					String columnType = "text";
					String cellStringValue = "";
					Double cellNumericValue = null;
					ExcelCol excelColumn;

					if (columnIndex < headers.size()) {
						/*
						If the cell isn't blank then check if the cell type
						is string, numeric, or boolean and set the variables
						accordingly. This fixes the issue with getNumericCellValue()
						being returned in scientific notation when converting to a
						string
						 */
						if(!cellType.equals(CellType.BLANK)) {
							if(cellType.equals(CellType.STRING)) {
								cellStringValue = cell.getStringCellValue().trim();
							} else if (cellType.equals(CellType.NUMERIC)) {
								columnType = "numeric";
								cellNumericValue = cell.getNumericCellValue();
							} else if (cellType.equals(CellType.BOOLEAN)) {
								cellStringValue = String.valueOf(cell.getBooleanCellValue()).trim();
							}
						}

						if (!cellStringValue.equals("") && !hasRowData) {
							hasRowData = true;
						}

						if(columnType.equals("text")) {
							excelColumn = new ExcelCol(columnIndex, cellStringValue);
						} else {
							excelColumn = new ExcelCol(columnIndex, cellNumericValue);
						}
						excelCols.add(excelColumn);
					}
				}
				// If atleast one column in a row has data we add the row to excelRows
				if (hasRowData) {
					ExcelRow excelRow = new ExcelRow(excelCols, row.getRowNum(), row.getRowNum());
					excelRows.add(excelRow);
				}
			}
			return new ExcelData(headers, excelRows, excelSheetNames, false);
		} catch (IOException e) {
			logger.error("Error opening file" + outputFile.getLabel());
		}
		// Should only reach here if the file could not be opened
		// at which point the ExcelData dto will just return null
		// values and set the parseError to true which the ui handles
		return new ExcelData(null, null, null, true);
	}

	/**
	 * Extract the headers from an excel file.
	 *
	 * @param row {@link Row} First row from the excel file.
	 * @return {@link List} of {@link ExcelHeader} header values.
	 */
	public static List<ExcelHeader> getWorkbookHeaders(Row row) {
		List<ExcelHeader> headers = new ArrayList<>();
		// Get the column headers
		Iterator<Cell> headerIterator = row.cellIterator();
		while (headerIterator.hasNext()) {
			Cell headerCell = headerIterator.next();
			CellType cellType = headerCell.getCellType();
			int columnIndex = headerCell.getColumnIndex();

			String headerValue;
			if (cellType.equals(CellType.STRING)) {
				headerValue = headerCell.getStringCellValue().trim();
			} else {
				headerValue = String.valueOf(headerCell.getNumericCellValue()).trim();
			}
			ExcelHeader excelHeader = new ExcelHeader(headerValue, columnIndex, Integer.toString(columnIndex));
			headers.add(excelHeader);
		}
		return headers;
	}

	/**
	 * Extract the sheet names from the excel workbook
	 *
	 * @param workbook {@link Workbook} The excel workbook to get sheet names from
	 * @return {@link List} of {@link String} sheet names.
	 */
	public static List<String> getExcelSheetNames(Workbook workbook) {
		List<String> sheetNames = new ArrayList<>();
		for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
			sheetNames.add(workbook.getSheetName(i));
		}
		return sheetNames;
	}

	/**
	 * From (http://stackoverflow.com/questions/3758606/how-to-convert-byte-size- into-human-readable-format-in-java)
	 *
	 * @param bytes The {@link Long} size of the file in bytes.
	 * @param si    {@link Boolean} true to use si units
	 * @return A human readable {@link String} representation of the file size.
	 */
	public static String humanReadableByteCount(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit)
			return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

	/**
	 * Determine if a file is a zip file.
	 *
	 * @param path The {@link Path} to the file to test.
	 * @return A boolean indicating whether the file is a zip file.
	 * @throws IOException If an invalid {@link Path} is passed
	 */
	public static boolean isZippedFile(final Path path) throws IOException {
		try (final InputStream in = new FileInputStream(path.toString());
				final ZipInputStream z = new ZipInputStream(in);) {
			// If we can read an entry we know that it is a zip
			return z.getNextEntry() != null;
		} catch (ZipException ignored) {
			return false;
		}
	}
}
