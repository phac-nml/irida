package ca.corefacility.bioinformatics.irida.ria.web.projects.metadata;

import java.util.*;

import javax.servlet.http.HttpSession;

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
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxSuccessResponse;
import ca.corefacility.bioinformatics.irida.ria.web.errors.SavedMetadataException;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.SavedMetadataErrorResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIMetadataImportService;;

/**
 * This class is designed to be used for bulk actions on {@link MetadataEntry}
 * within a {@link Project}.
 */
@Controller
@RequestMapping("/ajax/projects/sample-metadata/upload")
public class ProjectSampleMetadataAjaxController {
	private final UIMetadataImportService metadataImportService;

	@Autowired
	public ProjectSampleMetadataAjaxController(UIMetadataImportService metadataImportService) {
		this.metadataImportService = metadataImportService;
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
	 * @return {@link SampleMetadataStorage} which includes a {@link List} of
	 *         headers and rows from the csv or excel file.
	 * @throws Exception
	 *             if there is an error reading the file
	 */
	@PostMapping("/file")
	@ResponseBody
	public ResponseEntity<SampleMetadataStorage> createProjectSampleMetadata(HttpSession session,
			@RequestParam Long projectId, @RequestParam("file") MultipartFile file) throws Exception {
		return ResponseEntity.ok(metadataImportService.createProjectSampleMetadata(session, projectId, file));
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
	 * @return a complete message.
	 */
	@PutMapping("/setSampleColumn")
	@ResponseBody
	public ResponseEntity<AjaxResponse> setProjectSampleMetadataSampleId(HttpSession session,
			@RequestParam Long projectId, @RequestParam String sampleNameColumn) {
		return ResponseEntity.ok(new AjaxSuccessResponse(
				metadataImportService.setProjectSampleMetadataSampleId(session, projectId, sampleNameColumn)));
	}

	/**
	 * Save uploaded metadata from the session into IRIDA.
	 *
	 * @param locale
	 *            {@link Locale} of the current user.
	 * @param session
	 *            {@link HttpSession}
	 * @param projectId
	 *            {@link Long} identifier for the current project
	 * @param sampleNames
	 *            {@link List} of {@link String} sample names
	 * @return {@link String} message of how many samples were created and/or
	 *         updated.
	 */
	@PostMapping("/save")
	@ResponseBody
	public ResponseEntity<AjaxResponse> saveProjectSampleMetadata(Locale locale, HttpSession session,
			@RequestParam Long projectId, @RequestParam List<String> sampleNames) {
		try {
			return ResponseEntity.ok(new AjaxSuccessResponse(
					metadataImportService.saveProjectSampleMetadata(locale, session, projectId, sampleNames)));
		} catch (SavedMetadataException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new SavedMetadataErrorResponse(e.getStorage()));
		}
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
	@DeleteMapping("/clear")
	public void clearProjectSampleMetadata(HttpSession session, @RequestParam Long projectId) {
		metadataImportService.clearProjectSampleMetadata(session, projectId);
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
	@GetMapping("/getMetadata")
	@ResponseBody
	public ResponseEntity<SampleMetadataStorage> getProjectSampleMetadata(HttpSession session,
			@RequestParam Long projectId) {
		return ResponseEntity.ok(metadataImportService.getProjectSampleMetadata(session, projectId));
	}
}
