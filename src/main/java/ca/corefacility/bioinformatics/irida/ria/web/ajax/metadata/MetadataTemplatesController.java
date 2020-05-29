package ca.corefacility.bioinformatics.irida.ria.web.ajax.metadata;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ProjectMetadataTemplate;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIMetadataTemplateService;

@RestController
@RequestMapping("/ajax/metadata-templates")
public class MetadataTemplatesController {
	private final UIMetadataTemplateService service;

	@Autowired
	public MetadataTemplatesController(UIMetadataTemplateService service) {
		this.service = service;
	}

	@RequestMapping("")
	public ResponseEntity<List<ProjectMetadataTemplate>> getProjectMetadataTemplates(@RequestParam Long projectId) {
		return ResponseEntity.ok(service.getProjectMetadataTemplates(projectId));
	}
}
