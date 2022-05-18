package ca.corefacility.bioinformatics.irida.ria.web.projects;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.model.export.NCBIPlatformModel;

@RestController
@RequestMapping("/ajax/projects/{projectId}/ncbi")
public class ProjectNCBIExportAjaxController {

	@GetMapping("/platforms")
	public NCBIPlatformModel getNCBIPlatforms() {
		return new NCBIPlatformModel();
	}
}

