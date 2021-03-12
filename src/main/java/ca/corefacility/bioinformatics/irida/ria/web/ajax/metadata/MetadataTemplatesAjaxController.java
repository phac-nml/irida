package ca.corefacility.bioinformatics.irida.ria.web.ajax.metadata;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ProjectMetadataTemplate;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxSuccessResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.metadata.dto.CreateMetadataTemplateRequest;
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
	@GetMapping("")
	public ResponseEntity<List<ProjectMetadataTemplate>> getProjectMetadataTemplates(@RequestParam Long projectId) {
		return ResponseEntity.ok(service.getProjectMetadataTemplates(projectId));
	}

	@PostMapping("")
	public ResponseEntity<ProjectMetadataTemplate> createNewMetadataTemplate(@RequestBody CreateMetadataTemplateRequest request, @RequestParam Long projectId) {
		return ResponseEntity.ok(service.createMetadataTemplate(request, projectId));
	}

	@GetMapping("/{templateId}")
	public ResponseEntity<MetadataTemplate> getMetadataTemplate(@PathVariable Long templateId) {
		return ResponseEntity.ok(service.getMetadataTemplate(templateId));
	}

	@PutMapping("/{templateId}")
	public ResponseEntity<AjaxResponse> updatedMetadataTemplate(@RequestBody MetadataTemplate template) {
		service.updateMetadataTemplate(template);
		return ResponseEntity.ok(new AjaxSuccessResponse("__Template has been saved"));
	}

	@DeleteMapping("/{templateId}")
	public ResponseEntity<AjaxResponse> deleteMetadataTemplate(@PathVariable Long templateId,
			@RequestParam Long projectId) {
		service.deleteMetadataTemplate(templateId, projectId);
		return ResponseEntity.ok(new AjaxSuccessResponse("__Removed template"));
	}

	@PostMapping("/{templateId}/set-project-default")
	public ResponseEntity<AjaxResponse> setDefaultMetadataTemplate(@PathVariable Long templateId,
			@RequestParam Long projectId) {
		service.setDefaultMetadataTemplate(templateId, projectId);
		return ResponseEntity.ok(new AjaxSuccessResponse("__Set default template"));
	}

	@PostMapping("/remove-project-default")
	public ResponseEntity<AjaxResponse> removeDefaultMetadataTemplate(@RequestParam Long projectId) {
		service.removeDefaultMetadataTemplate(projectId);
		return ResponseEntity.ok(new AjaxSuccessResponse("__Removed default template"));
	}

}
