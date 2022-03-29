package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.ria.web.models.tables.AntTableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectCartSample;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectSampleTableItem;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectSamplesTableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.services.UISampleService;

/**
 * UI Ajax Controller for the project samples page.
 */
@RestController
@RequestMapping("/ajax/project-samples/{projectId}")
public class AjaxSamplesController {
	private UISampleService uiSampleService;

	@Autowired
	public AjaxSamplesController(UISampleService uiSampleService) {
		this.uiSampleService = uiSampleService;
	}

	/**
	 * Returns a Page of samples for a project based on the information in the {@link ProjectSamplesTableRequest}
	 *
	 * @param projectId Identifier for the current project
	 * @param request   Information about the current state of the project samples table.
	 * @return The Page of samples
	 */
	@PostMapping("")
	public ResponseEntity<AntTableResponse<ProjectSampleTableItem>> getPagedProjectSamples(@PathVariable Long projectId,
			@RequestBody ProjectSamplesTableRequest request) {
		return ResponseEntity.ok(uiSampleService.getPagedProjectSamples(projectId, request));
	}

	@PostMapping("/ids")
	public ResponseEntity<List<ProjectCartSample>> getProjectSamplesIds(@PathVariable Long projectId,
			@RequestBody ProjectSamplesTableRequest request) {
		return ResponseEntity.ok(uiSampleService.getFilteredProjectSamples(projectId, request));
	}
}
