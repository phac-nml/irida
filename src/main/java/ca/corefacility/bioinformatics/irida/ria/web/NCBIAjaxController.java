package ca.corefacility.bioinformatics.irida.ria.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.NcbiExportSubmissionAdminTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.NcbiExportSubmissionTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.export.NcbiSubmissionModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UINcbiService;

/**
 * Spring Ajax Controller to handle NCBI requests.
 */
@RestController
@RequestMapping("/ajax/ncbi")
public class NCBIAjaxController {
	private final UINcbiService service;

	@Autowired
	public NCBIAjaxController(UINcbiService service) {
		this.service = service;
	}

	/**
	 * Get a {@link List} of all NCBI Export Submissions on a Project
	 *
	 * @param projectId Identifier for a Project
	 * @return {@link List} of {@link NcbiExportSubmissionAdminTableModel}
	 */
	@RequestMapping("/project/{projectId}/list")
	public ResponseEntity<List<NcbiExportSubmissionTableModel>> getNCBIExportsForProject(@PathVariable Long projectId) {
		return ResponseEntity.ok(service.getNCBIExportsForProject(projectId));
	}

	/**
	 * Get the details of a specific NCBI SRA submission
	 *
	 * @param projectId Identifier for the current project
	 * @param exportId  Identifier for the NCBI SRA Submission
	 * @return details about the submission
	 */
	@GetMapping("/project/{projectId}/details/{exportId}")
	public ResponseEntity<NcbiSubmissionModel> getExportDetails(@PathVariable Long projectId,
			@PathVariable Long exportId) {
		return ResponseEntity.ok(service.getExportDetails(exportId));
	}

	/**
	 * Get a paged list of NCBI Export Submission based on the current page information
	 *
	 * @param request {@link TableRequest} containing details about the current page
	 * @return {@link TableResponse} of NCBI Export Submissions
	 */
	@RequestMapping("/list")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<TableResponse<NcbiExportSubmissionAdminTableModel>> getNCBIExportsForAdmin(
			@RequestBody TableRequest request) {
		return ResponseEntity.ok(service.getNCBIExportsForAdmin(request));
	}
}
