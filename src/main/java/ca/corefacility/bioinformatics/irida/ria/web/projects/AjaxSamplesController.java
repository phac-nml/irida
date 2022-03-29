package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.ria.web.models.tables.AntTableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectSamplesTableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.projects.settings.dto.AssociatedProject;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIAssociatedProjectsService;
import ca.corefacility.bioinformatics.irida.ria.web.services.UISampleService;

@RestController
@RequestMapping("/ajax/project-samples/{projectId}")
public class AjaxSamplesController {
	private UIAssociatedProjectsService uiAssociatedProjectsService;
	private UISampleService uiSampleService;

	@Autowired
	public AjaxSamplesController(UIAssociatedProjectsService uiAssociatedProjectsService,
			UISampleService uiSampleService) {
		this.uiAssociatedProjectsService = uiAssociatedProjectsService;
		this.uiSampleService = uiSampleService;
	}

	@PostMapping("")
	public ResponseEntity<AntTableResponse> getProjectSamples(@PathVariable Long projectId,
			@RequestBody ProjectSamplesTableRequest request) {
		return ResponseEntity.ok(uiSampleService.getPagedProjectSamples(projectId, request));
	}

	@GetMapping("/associated")
	public List<AssociatedProject> getAssociatedProjectsForProject(@PathVariable Long projectId) {
		return uiAssociatedProjectsService.getAssociatedProjectsForProject(projectId);
	}

	//	@PostMapping("/sampleIds")
	//	public List<Long> getProjectSamplesIds(@PathVariable Long projectId, @RequestBody SampleIdsRequest request) {
	//		List<Long> ids = request.getAssociated();
	//		ids.add(projectId);
	//		// I HAVE NO IDEA WHAT TO DO NOW!!!
	//		return uiSampleService.getSampleIdsForProject(ids);
	//	}
}
