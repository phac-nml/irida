package ca.corefacility.bioinformatics.irida.ria.web.export;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.model.export.NCBILibrarySource;
import ca.corefacility.bioinformatics.irida.model.export.NcbiLibrarySelection;
import ca.corefacility.bioinformatics.irida.model.export.NcbiLibraryStrategy;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.NcbiExportSubmissionAdminTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.NcbiExportSubmissionTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.export.dto.NCBIPlatformModel;
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

	@GetMapping("/platforms")
	public NCBIPlatformModel getNCBIPlatforms() {
		return new NCBIPlatformModel();
	}

	@GetMapping("/sources")
	public List<String> getNCBISources() {
		return Arrays.stream(NCBILibrarySource.values()).map(NCBILibrarySource::getValue).collect(Collectors.toList());
	}

	@GetMapping("/strategies")
	public List<String> getNCBIStrategies() {
		return Arrays.stream(NcbiLibraryStrategy.values())
				.map(NcbiLibraryStrategy::getValue)
				.collect(Collectors.toList());
	}

	@GetMapping("/selections")
	public List<String> getNCBISelection() {
		return Arrays.stream(NcbiLibrarySelection.values())
				.map(NcbiLibrarySelection::getValue)
				.collect(Collectors.toList());
	}
}
