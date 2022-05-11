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
import ca.corefacility.bioinformatics.irida.ria.web.errors.SavedMetadataException;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;

/**
 * UI service to handle importing metadata files, so they can be saved to the
 * session.
 */
@Component
public class UIMetadataImportService {

	private static final Logger logger = LoggerFactory.getLogger(UIMetadataImportService.class);
	private final MessageSource messageSource;
	private final ProjectService projectService;
	private final SampleService sampleService;
	private final MetadataTemplateService metadataTemplateService;
	private final UIMetadataFileImportService metadataFileImportService;

	@Autowired
	public UIMetadataImportService(MessageSource messageSource, ProjectService projectService,
			SampleService sampleService, MetadataTemplateService metadataTemplateService,
			UIMetadataFileImportService metadataFileImportService) {
		this.messageSource = messageSource;
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.metadataTemplateService = metadataTemplateService;
		this.metadataFileImportService = metadataFileImportService;
	}

	/**
	 * Upload CSV or Excel file containing sample metadata and extract the
	 * headers. The file is stored in the session until the column that
	 * corresponds to a {@link Sample} identifier has been sent.
	 *
	 * @param session
	 *            {@link HttpSession}
	 * @param projectId
	 *            {@link Long} identifier for the current {@link Project}
	 * @param file
	 *            {@link MultipartFile} The csv or excel file containing the
	 *            metadata.
	 * @return {@link Map} of headers and rows from the csv or excel file for
	 *         the user to select the header corresponding the {@link Sample}
	 *         identifier.
	 * @throws Exception
	 *             if there is an error reading the file
	 */
	public SampleMetadataStorage createProjectSampleMetadata(HttpSession session, Long projectId, MultipartFile file)
			throws Exception {
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
				// Should never reach here as the uploader limits to .csv, .xlsx
				// and .xlx files.
				throw new MetadataImportFileTypeNotSupportedError(extension);
			}

		} catch (FileNotFoundException e) {
			logger.debug("No file found for uploading an excel file of metadata.");
			throw e;
		} catch (IOException e) {
			logger.error("Error opening file" + file.getOriginalFilename());
			throw e;
		}

		session.setAttribute("pm-" + projectId, storage);
		return storage;
	}

	/**
	 * Add the metadata to specific {@link Sample} based on the selected column
	 * to correspond to the {@link Sample} id.
	 *
	 * @param session
	 *            {@link HttpSession}.
	 * @param projectId
	 *            {@link Long} identifier for the current {@link Project}.
	 * @param sampleNameColumn
	 *            {@link String} the header to used to represent the
	 *            {@link Sample} identifier.
	 * @return {@link String} containing a complete message.
	 */
	public String setProjectSampleMetadataSampleId(HttpSession session, Long projectId, String sampleNameColumn) {
		// Attempt to get the metadata from the sessions
		SampleMetadataStorage stored = (SampleMetadataStorage) session.getAttribute("pm-" + projectId);

		if (stored != null) {
			stored.setSampleNameColumn(sampleNameColumn);
			Project project = projectService.read(projectId);
			List<SampleMetadataStorageRow> rows = stored.getRows();
			List<SampleMetadataStorageRow> updatedRows = new ArrayList<>();

			// Get the metadata out of the table.
			for (SampleMetadataStorageRow row : rows) {
				try {
					// If this throws an error than the sample does not exist.
					Sample sample = sampleService.getSampleBySampleName(project, row.getEntryValue(sampleNameColumn));
					row.setFoundSampleId(sample.getId());
				} catch (EntityNotFoundException e) {
					row.setFoundSampleId(null);
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
	 * @param locale
	 *            {@link Locale} of the current user.
	 * @param session
	 *            {@link HttpSession}
	 * @param projectId
	 *            {@link Long} identifier for the current project
	 * @param sampleNames
	 *            {@link List} of {@link String} sample names
	 * @return {@link String} that returns a message and potential errors.
	 * @throws SavedMetadataException
	 *             if there is an error saving the metadata
	 */
	public String saveProjectSampleMetadata(Locale locale, HttpSession session, Long projectId,
			List<String> sampleNames) throws SavedMetadataException {
		List<String> DEFAULT_HEADERS = ImmutableList.of(
				messageSource.getMessage("project.samples.table.sample-id", new Object[] {}, locale),
				messageSource.getMessage("project.samples.table.id", new Object[] {}, locale),
				messageSource.getMessage("project.samples.table.modified-date", new Object[] {}, locale),
				messageSource.getMessage("project.samples.table.modified", new Object[] {}, locale),
				messageSource.getMessage("project.samples.table.created-date", new Object[] {}, locale),
				messageSource.getMessage("project.samples.table.created", new Object[] {}, locale),
				messageSource.getMessage("project.samples.table.coverage", new Object[] {}, locale),
				messageSource.getMessage("project.samples.table.project-id", new Object[] {}, locale));
		Project project = projectService.read(projectId);
		SampleMetadataStorage stored = (SampleMetadataStorage) session.getAttribute("pm-" + projectId);
		boolean hasErrors = false;
		String message;
		int samplesUpdatedCount = 0;
		int samplesCreatedCount = 0;

		if (sampleNames != null) {
			String sampleNameColumn = stored.getSampleNameColumn();

			for (String sampleName : sampleNames) {
				try {
					Set<MetadataEntry> metadataEntrySet = new HashSet<>();
					SampleMetadataStorageRow row = stored.getRow(sampleName, sampleNameColumn);
					String name = row.getEntryValue(sampleNameColumn);
					Sample sample = null;

					if (row.getFoundSampleId() != null) {
						sample = sampleService.getSampleBySampleName(project, name);
						samplesUpdatedCount++;
					} else {
						sample = new Sample(name);
						projectService.addSampleToProject(project, sample, true);
						samplesCreatedCount++;
					}

					// Need to overwrite duplicate keys
					for (Map.Entry<String, String> entry : row.getEntry().entrySet()) {
						// Make sure we are not saving non-metadata items.
						if (!DEFAULT_HEADERS.contains(entry.getKey()) && !sampleNameColumn.contains(entry.getKey())) {
							MetadataTemplateField key = metadataTemplateService
									.readMetadataFieldByLabel(entry.getKey());

							if (key == null) {
								key = metadataTemplateService
										.saveMetadataField(new MetadataTemplateField(entry.getKey(), "text"));
							}

							metadataEntrySet.add(new MetadataEntry(entry.getValue(), "text", key));
						}
					}

					// Save metadata back to the sample
					sampleService.mergeSampleMetadata(sample, metadataEntrySet);
					row.setSaved(true);
				} catch (Exception e) {
					SampleMetadataStorageRow row = stored.getRow(sampleName, sampleNameColumn);
					row.setError(e.getMessage());
					row.setSaved(false);
					hasErrors = true;
				}
			}
		}

		if (hasErrors) {
			throw new SavedMetadataException(stored);
		}

		message = ((samplesUpdatedCount == 1)
				? messageSource.getMessage("server.metadataimport.results.save.success.single-updated",
						new Object[] { samplesUpdatedCount }, locale)
				: messageSource.getMessage("server.metadataimport.results.save.success.multiple-updated",
						new Object[] { samplesUpdatedCount }, locale));
		message += (samplesCreatedCount == 1)
				? messageSource.getMessage("server.metadataimport.results.save.success.single-created",
						new Object[] { samplesCreatedCount }, locale)
				: messageSource.getMessage("server.metadataimport.results.save.success.multiple-created",
						new Object[] { samplesCreatedCount }, locale);

		return message;
	}

	/**
	 * Clear any uploaded sample metadata stored into the session.
	 *
	 * @param session
	 *            {@link HttpSession}
	 * @param projectId
	 *            identifier for the {@link Project} currently uploaded metadata
	 *            to.
	 */
	public void clearProjectSampleMetadata(HttpSession session, Long projectId) {
		session.removeAttribute("pm-" + projectId);
	}

	/**
	 * Get the currently stored metadata.
	 *
	 * @param session
	 *            {@link HttpSession}
	 * @param projectId
	 *            {@link Long} identifier for the current {@link Project}
	 * @return the currently stored {@link SampleMetadataStorage}
	 */
	public SampleMetadataStorage getProjectSampleMetadata(HttpSession session, Long projectId) {
		return (SampleMetadataStorage) session.getAttribute("pm-" + projectId);
	}
}
