package ca.corefacility.bioinformatics.irida.ria.web.projects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.ria.web.models.tables.AntTableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectSamplesTableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.services.UISampleService;

@RestController
@RequestMapping("/ajax/project-samples/{projectId}")
public class AjaxSamplesController {
	private UISampleService uiSampleService;

	@Autowired
	public AjaxSamplesController(UISampleService uiSampleService) {
		this.uiSampleService = uiSampleService;
	}

	@PostMapping("")
	public ResponseEntity<AntTableResponse> getProjectSamples(@PathVariable Long projectId,
			@RequestBody ProjectSamplesTableRequest request) {
		return ResponseEntity.ok(uiSampleService.getPagedProjectSamples(projectId, request));
	}

	//	@PostMapping("/sampleIds")
	//	public List<Long> getProjectSamplesIds(@PathVariable Long projectId, @RequestBody SampleIdsRequest request) {
	//		List<Long> ids = request.getAssociated();
	//		ids.add(projectId);
	//		// I HAVE NO IDEA WHAT TO DO NOW!!!
	//		return uiSampleService.getSampleIdsForProject(ids);
	//	}
}
