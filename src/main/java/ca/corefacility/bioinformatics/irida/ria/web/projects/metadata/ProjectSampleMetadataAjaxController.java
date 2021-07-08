package ca.corefacility.bioinformatics.irida.ria.web.projects.metadata;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.MetadataImportFileTypeNotSupportedError;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.ria.utilities.SampleMetadataStorage;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectControllerUtils;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIMetadataImportService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;

/**
 * This class is designed to be used for bulk actions on {@link MetadataEntry}
 * within a {@link Project}.
 */
@Controller
@RequestMapping("/ajax/projects/sample-metadata")
public class ProjectSampleMetadataAjaxController {
	private static final Logger logger = LoggerFactory.getLogger(ProjectSampleMetadataAjaxController.class);
	private final MessageSource messageSource;
	private final ProjectService projectService;
	private final SampleService sampleService;
	private final MetadataTemplateService metadataTemplateService;
	private final ProjectControllerUtils projectControllerUtils;
	private final UIMetadataImportService metadataImportService;

	@Autowired
	public ProjectSampleMetadataAjaxController(MessageSource messageSource, ProjectService projectService,
			SampleService sampleService, MetadataTemplateService metadataTemplateService,
			ProjectControllerUtils projectControllerUtils, UIMetadataImportService metadataImportService) {
		this.messageSource = messageSource;
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.metadataTemplateService = metadataTemplateService;
		this.projectControllerUtils = projectControllerUtils;
		this.metadataImportService = metadataImportService;
	}

	/**
	 * Upload CSV or Excel file containing sample metadata and extract the headers.  The file is stored in the session until
	 * the column that corresponds to a {@link Sample} identifier has been sent.
	 *
	 * @param session   {@link HttpSession}
	 * @param projectId {@link Long} identifier for the current {@link Project}
	 * @param file      {@link MultipartFile} The csv or excel file containing the metadata.
	 * @return {@link Map} of headers and rows from the csv or excel file for the user to select the header corresponding the
	 * {@link Sample} identifier.
	 */
	@RequestMapping(value = "/upload/file", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<SampleMetadataStorage> createProjectSampleMetadata(HttpSession session,
			@RequestParam long projectId, @RequestParam("file") MultipartFile file) {
		// We want to return a list of the table headers back to the UI.
		SampleMetadataStorage storage = new SampleMetadataStorage();
		try {
			String filename = file.getOriginalFilename();
			byte[] byteArr = file.getBytes();
			InputStream inputStream = new ByteArrayInputStream(byteArr);

			String extension = Files.getFileExtension(filename);

			// Check the file type
			switch (extension) {
			case "csv":
				storage = metadataImportService.parseCSV(projectId, inputStream);
				break;
			case "xlsx":
			case "xls":
				storage = metadataImportService.parseExcel(projectId, inputStream, extension);
				break;
			default:
				// Should never reach here as the uploader limits to .csv, .xlsx and .xlx files.
				throw new MetadataImportFileTypeNotSupportedError(extension);
			}
			inputStream.close();
		} catch (FileNotFoundException e) {
			logger.debug("No file found for uploading an excel file of metadata.");
		} catch (IOException e) {
			logger.error("Error opening file" + file.getOriginalFilename());
		}

		session.setAttribute("pm-" + projectId, storage);
		return ResponseEntity.ok(storage);
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
	public ResponseEntity<Map<String, Object>> setProjectSampleMetadataSampleId(HttpSession session,
			@RequestParam long projectId, @RequestParam String sampleNameColumn) {
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

		return ResponseEntity.ok(ImmutableMap.of("result", "complete"));
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
	public ResponseEntity<Map<String, Object>> saveProjectSampleMetadata(Locale locale, HttpSession session,
			@RequestParam long projectId) {
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

					Set<MetadataEntry> metadataEntrySet = new HashSet<>();

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

							metadataEntrySet.add(new MetadataEntry(entry.getValue(), "text", key));
						}
					}

					// Save metadata back to the sample
					sampleService.mergeSampleMetadata(sample, metadataEntrySet);
				}

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
			return ResponseEntity.ok(ImmutableMap.of("success",
					messageSource.getMessage("metadata.results.save.success", new Object[] { found.size() }, locale)));
		}
		return ResponseEntity.badRequest()
				.body(errors);
	}

	/**
	 * Clear any uploaded sample metadata stored into the session.
	 *
	 * @param session   {@link HttpSession}
	 * @param projectId identifier for the {@link Project} currently uploaded metadata to.
	 */
	@RequestMapping("/upload/clear")
	public void clearProjectSampleMetadata(HttpSession session, @RequestParam long projectId) {
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
	public ResponseEntity<SampleMetadataStorage> getProjectSampleMetadata(HttpSession session,
			@RequestParam long projectId) {
		return ResponseEntity.ok((SampleMetadataStorage) session.getAttribute("pm-" + projectId));
	}
}
