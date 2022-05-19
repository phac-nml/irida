package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.model.export.NCBILibrarySource;
import ca.corefacility.bioinformatics.irida.model.export.NCBIPlatformModel;

@RestController
@RequestMapping("/ajax/projects/{projectId}/ncbi")
public class ProjectNCBIExportAjaxController {

	@GetMapping("/platforms")
	public NCBIPlatformModel getNCBIPlatforms() {
		return new NCBIPlatformModel();
	}

	@GetMapping("/sources")
	public List<String> getNCBISources() {
		return Arrays.stream(NCBILibrarySource.values()).map(NCBILibrarySource::getValue).collect(Collectors.toList());
	}
}

