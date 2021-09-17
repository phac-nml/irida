package ca.corefacility.bioinformatics.irida.ria.web.projects.metadata;

import java.util.*;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.ria.utilities.SampleMetadataStorage;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxErrorResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxSuccessResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIMetadataImportService;;

/**
 * This class is designed to be used for bulk actions on {@link MetadataEntry}
 * within a {@link Project}.
 */
@Controller
@RequestMapping("/ajax/projects/sample-metadata/upload")
public class ProjectSampleMetadataAjaxController {
	private static final Logger logger = LoggerFactory.getLogger(ProjectSampleMetadataAjaxController.class);
	private final UIMetadataImportService metadataImportService;

	@Autowired
	public ProjectSampleMetadataAjaxController(UIMetadataImportService metadataImportService) {
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
	@PostMapping("/file")
	@ResponseBody
	public ResponseEntity<SampleMetadataStorage> createProjectSampleMetadata(HttpSession session,
			@RequestParam Long projectId, @RequestParam("file") MultipartFile file) {
		return ResponseEntity.ok(metadataImportService.createProjectSampleMetadata(session, projectId, file));
	}

	/**
	 * Add the metadata to specific {@link Sample} based on the selected column to correspond to the {@link Sample} id.
	 *
	 * @param session          {@link HttpSession}.
	 * @param projectId        {@link Long} identifier for the current {@link Project}.
	 * @param sampleNameColumn {@link String} the header to used to represent the {@link Sample} identifier.
	 * @return {@link Map} containing a complete message.
	 */
	@PostMapping("/setSampleColumn")
	@ResponseBody
	public ResponseEntity<AjaxResponse> setProjectSampleMetadataSampleId(HttpSession session,
			@RequestParam Long projectId, @RequestParam String sampleNameColumn) {
		return ResponseEntity.ok(new AjaxSuccessResponse(
				metadataImportService.setProjectSampleMetadataSampleId(session, projectId, sampleNameColumn)));
	}

	/**
	 * Save uploaded metadata to the
	 *
	 * @param locale      {@link Locale} of the current user.
	 * @param session     {@link HttpSession}
	 * @param projectId   {@link Long} identifier for the current project
	 * @param sampleNames {@link List} of {@link String} sample names
	 * @return {@link Map} of potential errors.
	 */
	@PostMapping("/save")
	@ResponseBody
	public ResponseEntity<AjaxResponse> saveProjectSampleMetadata(Locale locale, HttpSession session,
			@RequestParam Long projectId, @RequestParam List<String> sampleNames) {
		try {
			return ResponseEntity.ok(new AjaxSuccessResponse(
					metadataImportService.saveProjectSampleMetadata(locale, session, projectId, sampleNames)
							.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(new AjaxErrorResponse(e.getMessage()));
		}
	}

	/**
	 * Clear any uploaded sample metadata stored into the session.
	 *
	 * @param session   {@link HttpSession}
	 * @param projectId identifier for the {@link Project} currently uploaded metadata to.
	 */
	@GetMapping("/clear")
	public void clearProjectSampleMetadata(HttpSession session, @RequestParam Long projectId) {
		metadataImportService.clearProjectSampleMetadata(session, projectId);
	}

	/**
	 * Get the currently stored metadata.
	 *
	 * @param session   {@link HttpSession}
	 * @param projectId {@link Long} identifier for the current {@link Project}
	 * @return the currently stored {@link SampleMetadataStorage}
	 */
	@GetMapping("/getMetadata")
	@ResponseBody
	public ResponseEntity<SampleMetadataStorage> getProjectSampleMetadata(HttpSession session,
			@RequestParam Long projectId) {
		return ResponseEntity.ok(metadataImportService.getProjectSampleMetadata(session, projectId));
	}
}
