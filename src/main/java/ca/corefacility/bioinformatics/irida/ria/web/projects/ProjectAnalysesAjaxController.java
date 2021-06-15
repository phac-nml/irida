package ca.corefacility.bioinformatics.irida.ria.web.projects;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling all ajax requests for Project Analyses Outputs.
 */
@RestController
@RequestMapping("/ajax/projects/analyses")
public class ProjectAnalysesAjaxController {

	@GetMapping("/shared-outputs")
	public void getSharedSingleSampleOutputs(@RequestParam Long projectId) {
	}

	@GetMapping("/automated-outputs")
	public void getAutomatedSingleSampleOutputs(@RequestParam Long projectId) {
	}

	@GetMapping("/download-shared-outputs")
	public void downloadSharedSingleSampleOutputs(@RequestParam Long projectId) {
	}

	@GetMapping("/download-automated-outputs")
	public void downloadAutomatedSingleSampleOutputs(@RequestParam Long projectId) {
	}
}
