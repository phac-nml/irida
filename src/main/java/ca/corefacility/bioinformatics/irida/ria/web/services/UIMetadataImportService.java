package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.MetadataImportFileTypeNotSupportedError;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.ria.utilities.SampleMetadataStorage;
import ca.corefacility.bioinformatics.irida.ria.utilities.SampleMetadataStorageRow;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectControllerUtils;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectSampleMetadataResponse;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;

/**
 * UI service to handle importing metadata files so they can be saved to the session.
 */
@Component
public class UIMetadataImportService {

	private static final Logger logger = LoggerFactory.getLogger(UIMetadataImportService.class);
	private final MessageSource messageSource;
	private final ProjectService projectService;
	private final SampleService sampleService;
	private final MetadataTemplateService metadataTemplateService;
	private final ProjectControllerUtils projectControllerUtils;
	private final UIMetadataFileImportService metadataFileImportService;

	@Autowired
	public UIMetadataImportService(MessageSource messageSource, ProjectService projectService,
			SampleService sampleService, MetadataTemplateService metadataTemplateService,
			ProjectControllerUtils projectControllerUtils, UIMetadataFileImportService metadataFileImportService) {
		this.messageSource = messageSource;
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.metadataTemplateService = metadataTemplateService;
		this.projectControllerUtils = projectControllerUtils;
		this.metadataFileImportService = metadataFileImportService;
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
	public SampleMetadataStorage createProjectSampleMetadata(HttpSession session, long projectId, MultipartFile file) {
		// We want to return a list of the table headers back to the UI.
		SampleMetadataStorage storage = new SampleMetadataStorage();
		try (InputStream inputStream = file.getInputStream()) {
			String filename = file.getOriginalFilename();
			String extension = Files.getFileExtension(filename);

			// Check the file type
			switch (extension) {
			case "csv":
				storage = metadataFileImportService.parseCSV(projectId, inputStream);
				break;
			case "xlsx":
			case "xls":
				storage = metadataFileImportService.parseExcel(projectId, inputStream, extension);
				break;
			default:
				// Should never reach here as the uploader limits to .csv, .xlsx and .xlx files.
				throw new MetadataImportFileTypeNotSupportedError(extension);
			}

		} catch (FileNotFoundException e) {
			logger.debug("No file found for uploading an excel file of metadata.");
		} catch (IOException e) {
			logger.error("Error opening file" + file.getOriginalFilename());
		}

		session.setAttribute("pm-" + projectId, storage);
		return storage;
	}

	/**
	 * Add the metadata to specific {@link Sample} based on the selected column to correspond to the {@link Sample} id.
	 *
	 * @param session          {@link HttpSession}.
	 * @param projectId        {@link Long} identifier for the current {@link Project}.
	 * @param sampleNameColumn {@link String} the header to used to represent the {@link Sample} identifier.
	 * @return {@link String} containing a complete message.
	 */
	public String setProjectSampleMetadataSampleId(HttpSession session, long projectId, String sampleNameColumn) {
		// Attempt to get the metadata from the sessions
		SampleMetadataStorage stored = (SampleMetadataStorage) session.getAttribute("pm-" + projectId);

		if (stored != null) {
			stored.setSampleNameColumn(sampleNameColumn);
			Project project = projectService.read(projectId);
			List<SampleMetadataStorageRow> rows = stored.getRows();
			List<SampleMetadataStorageRow> updatedRows = new ArrayList<>();

			// Get the metadata out of the table.
			for (SampleMetadataStorageRow row : rows) {
				SampleMetadataStorageRow updatedRow = row;
				try {
					// If this throws an error than the sample does not exist.
					Sample sample = sampleService.getSampleBySampleName(project, row.getEntryValue(sampleNameColumn));
					row.setFoundSampleId(sample.getId());
				} catch (EntityNotFoundException e) {
					logger.trace("Sample in project" + project.getId() + " is not found.", e);
				}
				updatedRows.add(row);
			}
			stored.setRows(updatedRows);
		}

		return "complete";
	}

	/**
	 * Save uploaded metadata
	 *
	 * @param locale    {@link Locale} of the current user.
	 * @param session   {@link HttpSession}
	 * @param projectId {@link Long} identifier for the current project
	 * @return {@link ProjectSampleMetadataResponse} that returns a message and potential errors.
	 */
	public ProjectSampleMetadataResponse saveProjectSampleMetadata(Locale locale, HttpSession session, long projectId) {
		List<String> DEFAULT_HEADERS = ImmutableList.of("Sample Id", "ID", "Modified Date", "Modified On",
				"Created Date", "Created On", "Coverage", "Project ID");
		Project project = projectService.read(projectId);
		ProjectSampleMetadataResponse response = new ProjectSampleMetadataResponse();
		SampleMetadataStorage stored = (SampleMetadataStorage) session.getAttribute("pm-" + projectId);

		if (stored == null) {
			response.setMessageKey("stored-error");
		}

		List<SampleMetadataStorageRow> found = stored.getFoundRows();

		if (found != null) {
			// Lets try to get a sample
			String sampleNameColumn = stored.getSampleNameColumn();
			List<String> errorList = new ArrayList<>();
			try {
				for (SampleMetadataStorageRow row : found) {

					String name = row.getEntryValue(sampleNameColumn);
					Sample sample = sampleService.getSampleBySampleName(project, name);
					Set<MetadataEntry> metadataEntrySet = new HashSet<>();

					// Need to overwrite duplicate keys
					for (Map.Entry<String, String> entry : row.getEntry()
							.entrySet()) {
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
				errorList.add(messageSource.getMessage("server.metadataimport.results.save.sample-not-found",
						new Object[] { e.getMessage() }, locale));
			}

			if (errorList.size() > 0) {
				response.setMessageKey("save-error");
				response.setErrorList(errorList);
			}
		} else {
			response.setMessageKey("found-error");
			response.setMessage(
					messageSource.getMessage("server.metadataimport.results.save.found-error", new Object[] {},
							locale));
		}

		if (response.getMessageKey() == null) {
			response.setMessageKey("success");
			response.setMessage(messageSource.getMessage("server.metadataimport.results.save.success",
					new Object[] { found.size() }, locale));
		}

		return response;
	}

	/**
	 * Clear any uploaded sample metadata stored into the session.
	 *
	 * @param session   {@link HttpSession}
	 * @param projectId identifier for the {@link Project} currently uploaded metadata to.
	 */
	public void clearProjectSampleMetadata(HttpSession session, long projectId) {
		session.removeAttribute("pm-" + projectId);
	}

	/**
	 * Get the currently stored metadata.
	 *
	 * @param session   {@link HttpSession}
	 * @param projectId {@link Long} identifier for the current {@link Project}
	 * @return the currently stored {@link SampleMetadataStorage}
	 */
	public SampleMetadataStorage getProjectSampleMetadata(HttpSession session, long projectId) {
		return (SampleMetadataStorage) session.getAttribute("pm-" + projectId);
	}
}
