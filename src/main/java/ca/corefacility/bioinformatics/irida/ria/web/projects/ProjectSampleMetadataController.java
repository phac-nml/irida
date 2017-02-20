package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.MetadataImportFileTypeNotSupportedError;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleMetadata;
import ca.corefacility.bioinformatics.irida.ria.utilities.SampleMetadataStorage;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;

/**
 * This class is designed to be used for bulk actions on {@link SampleMetadata}
 * within a {@link Project}.
 */
@Controller
@RequestMapping("/projects/{projectId}/sample-metadata")
public class ProjectSampleMetadataController {
	private static final Logger logger = LoggerFactory.getLogger(ProjectSampleMetadataController.class);
	private final MessageSource messageSource;
	private final MetadataTemplateService metadataTemplateService;
	private final ProjectControllerUtils projectControllerUtils;
	private final ProjectService projectService;
	private final SampleService sampleService;

	@Autowired
	public ProjectSampleMetadataController(MessageSource messageSource, MetadataTemplateService metadataTemplateService,
			ProjectControllerUtils projectControllerUtils, ProjectService projectService, SampleService sampleService) {
		this.messageSource = messageSource;
		this.metadataTemplateService = metadataTemplateService;
		this.projectControllerUtils = projectControllerUtils;
		this.projectService = projectService;
		this.sampleService = sampleService;
	}

	/**
	 * Get the page to create a new {@link MetadataTemplate}
	 *
	 * @param projectId
	 * 		the {@link Long} identifier for the current project
	 * @param model
	 * 		{@link Model}
	 * @param principal
	 * 		{@link Principal} currently logged in user
	 * @param locale
	 * 		{@link Locale} of the logged in user.
	 *
	 * @return {@link String} path to the page
	 */
	@RequestMapping("/templates/new")
	public String getCreateNewSampleMetadataTemplatePage(@PathVariable Long projectId, Model model, Principal principal,
			Locale locale) {
		// Set up the template information
		Project project = projectService.read(projectId);
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);
		model.addAttribute("templates", projectControllerUtils.getTemplateNames(locale, project));
		return "projects/project_samples_metadata_template";
	}

	/**
	 * Handle the page request to upload {@link Sample} metadata
	 *
	 * @param model
	 * 		{@link Model}
	 * @param projectId
	 * 		{@link Long} identifier for the current {@link Project}
	 *
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
	 * @param session
	 * 		{@link HttpSession}
	 * @param projectId
	 * 		{@link Long} identifier for the current {@link Project}
	 * @param file
	 * 		{@link MultipartFile} The excel file containing the metadata.
	 *
	 * @return {@link Map} of headers and rows from the excel file for the user to select the header corresponding the
	 * {@link Sample} identifier.
	 */
	@RequestMapping(value = "/upload/file", method = RequestMethod.POST)
	@ResponseBody
	public SampleMetadataStorage createProjectSampleMetadata(HttpSession session, @PathVariable long projectId,
			@RequestParam("file") MultipartFile file) throws MetadataImportFileTypeNotSupportedError {
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
				int headerCounter = 0;
				Map<String, String> rowMap = new HashMap<>();
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext() && headerCounter < headers.size()) {
					Cell cell = cellIterator.next();
					String cellValue = cell.getStringCellValue().trim();
					String header = headers.get(headerCounter).trim();
					rowMap.put(header, cellValue);
					headerCounter += 1;
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
	 * @param row
	 * 		{@link Row} First row from the excel file.
	 *
	 * @return {@link List} of {@link String} header values.
	 */
	private List<String> getWorkbookHeaders(Row row) {
		// We want to return a list of the table headers back to the UI.
		List<String> headers = new ArrayList<>();

		// Get the column headers
		Iterator<Cell> headerIterator = row.cellIterator();
		while (headerIterator.hasNext()) {
			Cell headerCell = headerIterator.next();
			String headerValue = headerCell.getStringCellValue().trim();

			// Don't want empty header values.
			if (!Strings.isNullOrEmpty(headerValue)) {
				headers.add(headerValue);
			}
		}
		return headers;
	}

	/**
	 * Add the metadata to specific {@link Sample} based on the selected column to correspond to the {@link Sample} id.
	 *
	 * @param session
	 * 		{@link HttpSession}.
	 * @param projectId
	 * 		{@link Long} identifier for the current {@link Project}.
	 * @param sampleNameColumn
	 * 		{@link String} the header to used to represent the {@link Sample} identifier.
	 *
	 * @return {@link Map} containing
	 */
	@RequestMapping(value = "/upload/setSampleColumn", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> setProjectSampleMetadataSampleId(HttpSession session, @PathVariable long projectId,
			@RequestParam String sampleNameColumn) {
		// Attempt to get the metadata from the sessions
		SampleMetadataStorage stored = (SampleMetadataStorage) session.getAttribute("pm-" + projectId);

		if (stored != null) {
			stored.saveSampleNameColumn(sampleNameColumn);
			Project project = projectService.read(projectId);
			List<Map<String, String>> rows = stored.getRows();

			// Remove 'rows' since they are now going to be sorted into found and not found.
			stored.removeRows();
			List<Map<String, String>> found = new ArrayList<>();
			List<Map<String, String>> missing = new ArrayList<>();

			// Get the metadata out of the table.
			for (Map<String, String> row : rows) {
				// Lets try to get a sample
				String sampleName = row.get(sampleNameColumn);
				try {
					Sample sample = sampleService.getSampleBySampleName(project, sampleName);
					row.put("identifier", String.valueOf(sample.getId()));
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
	 * @param locale
	 * 		{@link Locale} of the current user.
	 * @param session
	 * 		{@link HttpSession}
	 * @param projectId
	 * 		{@link Long} identifier for the current project
	 *
	 * @return {@link Map} of potential errors.
	 */
	@RequestMapping(value = "/upload/save", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> saveProjectSampleMetadata(Locale locale, HttpSession session,
			@PathVariable long projectId) {
		Map<String, Object> errors = new HashMap<>();

		SampleMetadataStorage stored = (SampleMetadataStorage) session.getAttribute("pm-" + projectId);
		if (stored == null) {
			errors.put("stored-error", true);
		}

		List<Map<String, String>> found = stored.getFound();
		if (found != null) {
			List<String> errorList = new ArrayList<>();
			for (Map<String, String> row : found) {
				try {
					Long id = Long.valueOf(row.get("identifier"));
					Sample sample = sampleService.read(id);
					SampleMetadata sampleMetadata = sampleService.getMetadataForSample(sample);
					if (sampleMetadata == null) {
						sampleMetadata = new SampleMetadata();
					}
					Map<String, Object> metadata = sampleMetadata.getMetadata();

					// Need to overwrite duplicate keys
					for (String item : row.keySet()) {
						metadata.put(item, ImmutableMap.of("value", row.get(item)));
					}

					// Save metadata back to the sample
					sampleMetadata.setMetadata(metadata);
					sampleService.saveSampleMetadaForSample(sample, sampleMetadata);
				} catch (EntityNotFoundException e) {
					// This really should not happen, but hey, you never know!
					errorList.add(messageSource.getMessage("metadata.results.save.sample-not-found",
							new Object[] { row.get(stored.getSampleNameColumn()) }, locale));
				}
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
	 * @param session
	 * 		{@link HttpSession}
	 * @param projectId
	 * 		identifier for the {@link Project} currently uploaded metadata to.
	 */
	@RequestMapping("/upload/clear")
	public void clearProjectSampleMetadata(HttpSession session, @PathVariable long projectId) {
		session.removeAttribute("pm-" + projectId);
	}

	/**
	 * Get the currently stored metadata.
	 *
	 * @param session
	 * 		{@link HttpSession}
	 * @param projectId
	 * 		{@link Long} identifier for the current {@link Project}
	 *
	 * @return
	 */
	@RequestMapping("/upload/getMetadata")
	@ResponseBody
	public SampleMetadataStorage getProjectSampleMetadata(HttpSession session, @PathVariable long projectId) {
		return (SampleMetadataStorage) session.getAttribute("pm-" + projectId);
	}
}
