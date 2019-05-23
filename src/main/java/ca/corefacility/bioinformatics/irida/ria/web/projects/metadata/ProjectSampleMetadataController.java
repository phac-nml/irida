package ca.corefacility.bioinformatics.irida.ria.web.projects.metadata;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.Map.Entry;

import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.MetadataImportFileTypeNotSupportedError;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.ria.utilities.SampleMetadataStorage;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;

/**
 * This class is designed to be used for bulk actions on {@link MetadataEntry}
 * within a {@link Project}.
 */
@Controller
@RequestMapping("/projects/{projectId}/sample-metadata")
public class ProjectSampleMetadataController {
	private static final Logger logger = LoggerFactory.getLogger(ProjectSampleMetadataController.class);
	private final MessageSource messageSource;
	private final ProjectService projectService;
	private final SampleService sampleService;
	private final MetadataTemplateService metadataTemplateService;

	@Autowired
	public ProjectSampleMetadataController(MessageSource messageSource, ProjectService projectService,
			SampleService sampleService, MetadataTemplateService metadataTemplateService) {
		this.messageSource = messageSource;
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.metadataTemplateService = metadataTemplateService;
	}

	/**
	 * Handle the page request to upload {@link Sample} metadata
	 *
	 * @param model     {@link Model}
	 * @param projectId {@link Long} identifier for the current {@link Project}
	 * @return {@link String} the path to the metadata import page
	 */
	@RequestMapping(value = "upload", method = RequestMethod.GET)
	public String getProjectSamplesMetadataUploadPage(final Model model, @PathVariable long projectId) {
		model.addAttribute("project", projectService.read(projectId));
		return "projects/project_samples_metadata_upload";
	}

	/**
	 * Upload Excel file containing sample metadata and extract the headers.  The file is stored in the session until
	 * the column that corresponds to a {@link Sample} identifier has been sent.
	 *
	 * @param session   {@link HttpSession}
	 * @param projectId {@link Long} identifier for the current {@link Project}
	 * @param file      {@link MultipartFile} The excel file containing the metadata.
	 * @return {@link Map} of headers and rows from the excel file for the user to select the header corresponding the
	 * {@link Sample} identifier.
	 */
	@RequestMapping(value = "/upload/file", method = RequestMethod.POST)
	@ResponseBody
	public SampleMetadataStorage createProjectSampleMetadata(HttpSession session, @PathVariable long projectId,
			@RequestParam("file") MultipartFile file) {
		// We want to return a list of the table headers back to the UI.
		SampleMetadataStorage storage = new SampleMetadataStorage();
		try {
			// Need an input stream
			String filename = file.getOriginalFilename();
			byte[] byteArr = file.getBytes();
			InputStream fis = new ByteArrayInputStream(byteArr);

			Workbook workbook;
			String extension = Files.getFileExtension(filename);

			// Check the type of workbook
			switch (extension) {
			case "xlsx":
				workbook = new XSSFWorkbook(fis);
				break;
			case "xls":
				workbook = new HSSFWorkbook(fis);
				break;
			default:
				// Should never reach here as the uploader limits to .xlsx and .xlx files.
				throw new MetadataImportFileTypeNotSupportedError(extension);
			}

			// Only look at the first sheet in the workbook as this should be the file we want.
			Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();

			List<String> headers = getWorkbookHeaders(rowIterator.next());
			storage.saveHeaders(headers);

			// Get the metadata out of the table.
			List<Map<String, String>> rows = new ArrayList<>();
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
							cell.setCellType(CellType.STRING);
							rowMap.put(header, cell.getStringCellValue());
						}
					}
				}
				rows.add(rowMap);
			}
			storage.saveRows(rows);

			fis.close();
		} catch (FileNotFoundException e) {
			logger.debug("No file found for uploading an excel file of metadata.");
		} catch (IOException e) {
			logger.error("Error opening file" + file.getOriginalFilename());
		}

		session.setAttribute("pm-" + projectId, storage);
		return storage;
	}

	/**
	 * Extract the headers from an excel file.
	 *
	 * @param row {@link Row} First row from the excel file.
	 * @return {@link List} of {@link String} header values.
	 */
	private List<String> getWorkbookHeaders(Row row) {
		// We want to return a list of the table headers back to the UI.
		List<String> headers = new ArrayList<>();

		// Get the column headers
		Iterator<Cell> headerIterator = row.cellIterator();
		while (headerIterator.hasNext()) {
			Cell headerCell = headerIterator.next();
			CellType cellType = headerCell.getCellTypeEnum();

			String headerValue;
			if (cellType.equals(CellType.STRING)) {
				headerValue = headerCell.getStringCellValue()
						.trim();
			} else {
				headerValue = String.valueOf(headerCell.getNumericCellValue())
						.trim();
			}

			// Leave empty headers for now, we will remove those columns later.
			headers.add(headerValue);
		}
		return headers;
	}

	/**
	 * Add the metadata to specific {@link Sample} based on the selected column to correspond to the {@link Sample} id.
	 *
	 * @param session          {@link HttpSession}.
	 * @param projectId        {@link Long} identifier for the current {@link Project}.
	 * @param sampleNameColumn {@link String} the header to used to represent the {@link Sample} identifier.
	 * @return {@link Map} containing
	 */
	@RequestMapping(value = "/upload/setSampleColumn", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> setProjectSampleMetadataSampleId(HttpSession session, @PathVariable long projectId,
			@RequestParam String sampleNameColumn) {
		// Attempt to get the metadata from the sessions
		SampleMetadataStorage stored = (SampleMetadataStorage) session.getAttribute("pm-" + projectId);

		if (stored != null) {
			stored.setSampleNameColumn(sampleNameColumn);
			Project project = projectService.read(projectId);
			List<Map<String, String>> rows = stored.getRows();

			// Remove 'rows' since they are now going to be sorted into found and not found.
			stored.removeRows();
			List<Map<String, String>> found = new ArrayList<>();
			List<Map<String, String>> missing = new ArrayList<>();

			// Get the metadata out of the table.
			for (Map<String, String> row : rows) {
				try {
					// If this throws an error than the sample does not exist.
					sampleService.getSampleBySampleName(project, row.get(sampleNameColumn));
					found.add(row);
				} catch (EntityNotFoundException e) {
					missing.add(row);
				}
			}

			stored.saveFound(found);
			stored.saveMissing(missing);
		}

		return ImmutableMap.of("result", "complete");
	}

	/**
	 * Save uploaded metadata to the
	 *
	 * @param locale    {@link Locale} of the current user.
	 * @param session   {@link HttpSession}
	 * @param projectId {@link Long} identifier for the current project
	 * @return {@link Map} of potential errors.
	 */
	@RequestMapping(value = "/upload/save", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> saveProjectSampleMetadata(Locale locale, HttpSession session,
			@PathVariable long projectId) {
		List<String> DEFAULT_HEADERS = ImmutableList.of("Sample Id", "ID", "Modified Date", "Modified On",
				"Created Date", "Created On", "Coverage", "Project ID");
		Map<String, Object> errors = new HashMap<>();
		Project project = projectService.read(projectId);

		SampleMetadataStorage stored = (SampleMetadataStorage) session.getAttribute("pm-" + projectId);
		if (stored == null) {
			errors.put("stored-error", true);
		}

		List<Sample> samplesToUpdate = new ArrayList<>();

		List<Map<String, String>> found = stored.getFound();
		if (found != null) {
			// Lets try to get a sample
			String sampleNameColumn = stored.getSampleNameColumn();
			List<String> errorList = new ArrayList<>();
			try {
				for (Map<String, String> row : found) {

					String name = row.get(sampleNameColumn);
					Sample sample = sampleService.getSampleBySampleName(project, name);
					row.remove(sampleNameColumn);

					Map<MetadataTemplateField, MetadataEntry> newData = new HashMap<>();

					// Need to overwrite duplicate keys
					for (Entry<String, String> entry : row.entrySet()) {
						// Make sure we are not saving non-metadata items.
						if (!DEFAULT_HEADERS.contains(entry.getKey())) {
							MetadataTemplateField key = metadataTemplateService.readMetadataFieldByLabel(
									entry.getKey());

							if (key == null) {
								key = metadataTemplateService.saveMetadataField(
										new MetadataTemplateField(entry.getKey(), "text"));
							}

							newData.put(key, new MetadataEntry(entry.getValue(), "text"));
						}
					}

					sample.mergeMetadata(newData);

					// Save metadata back to the sample

					samplesToUpdate.add(sample);

				}

				sampleService.updateMultiple(samplesToUpdate);
			} catch (EntityNotFoundException e) {
				// This really should not happen, but hey, you never know!
				errorList.add(messageSource.getMessage("metadata.results.save.sample-not-found",
						new Object[] { e.getMessage() }, locale));
			}

			if (errorList.size() > 0) {
				errors.put("save-errors", errorList);
			}
		} else {
			errors.put("found-error",
					messageSource.getMessage("metadata.results.save.found-error", new Object[] {}, locale));
		}
		if (errors.size() == 0) {
			return ImmutableMap.of("success",
					messageSource.getMessage("metadata.results.save.success", new Object[] { found.size() }, locale));
		}
		return errors;
	}

	/**
	 * Clear any uploaded sample metadata stored into the session.
	 *
	 * @param session   {@link HttpSession}
	 * @param projectId identifier for the {@link Project} currently uploaded metadata to.
	 */
	@RequestMapping("/upload/clear")
	public void clearProjectSampleMetadata(HttpSession session, @PathVariable long projectId) {
		session.removeAttribute("pm-" + projectId);
	}

	/**
	 * Get the currently stored metadata.
	 *
	 * @param session   {@link HttpSession}
	 * @param projectId {@link Long} identifier for the current {@link Project}
	 * @return the currently stored {@link SampleMetadataStorage}
	 */
	@RequestMapping("/upload/getMetadata")
	@ResponseBody
	public SampleMetadataStorage getProjectSampleMetadata(HttpSession session, @PathVariable long projectId) {
		return (SampleMetadataStorage) session.getAttribute("pm-" + projectId);
	}
}
