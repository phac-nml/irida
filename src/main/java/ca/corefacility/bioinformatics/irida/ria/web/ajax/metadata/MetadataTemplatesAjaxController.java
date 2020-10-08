package ca.corefacility.bioinformatics.irida.ria.web.ajax.metadata;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ProjectMetadataTemplate;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIMetadataTemplateService;

/**
 * Ajax controller for project metedata templates.
 */
@RestController
@RequestMapping("/ajax/metadata-templates")
public class MetadataTemplatesAjaxController {
	private final UIMetadataTemplateService service;

	@Autowired
	public MetadataTemplatesAjaxController(UIMetadataTemplateService service) {
		this.service = service;
	}

	/**
	 * Get a list of metadata templates for a specific project
	 * @param projectId Identifier for the project to get templates for.
	 * @return List of metadata templates with associate details.
	 */
	@RequestMapping("")
	public ResponseEntity<List<ProjectMetadataTemplate>> getProjectMetadataTemplates(@RequestParam Long projectId) {
		return ResponseEntity.ok(service.getProjectMetadataTemplates(projectId));
	}
}
