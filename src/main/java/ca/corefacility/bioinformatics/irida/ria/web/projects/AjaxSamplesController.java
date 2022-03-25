package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.ria.web.models.tables.AntTableRequest;

@RestController
@RequestMapping("/ajax/project-samples")
public class AjaxSamplesController {

	@PostMapping("")
	public void getProjectSamples(@RequestParam List<Long> projectIds, @RequestBody AntTableRequest request) {
		String foobar = "bax";
	}
}
