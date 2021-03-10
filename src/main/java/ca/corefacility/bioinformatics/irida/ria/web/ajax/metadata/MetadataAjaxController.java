package ca.corefacility.bioinformatics.irida.ria.web.ajax.metadata;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ProjectMetadataTemplate;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxErrorResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxSuccessResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.metadata.dto.CreateMetadataTemplateRequest;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIMetadataService;

/**
 * Ajax controller for project metedata templates.
 */
@RestController
@RequestMapping("/ajax/metadata")
public class MetadataAjaxController {
	private final UIMetadataService service;

	@Autowired
	public MetadataAjaxController(UIMetadataService service) {
		this.service = service;
	}

	/**
	 * Get a list of metadata templates for a specific project
	 * @param projectId Identifier for the project to get templates for.
	 * @return List of metadata templates with associate details.
	 */
	@GetMapping("/templates")
	public ResponseEntity<List<ProjectMetadataTemplate>> getProjectMetadataTemplates(@RequestParam Long projectId) {
		return ResponseEntity.ok(service.getProjectMetadataTemplates(projectId));
	}

	@PostMapping("/templates")
	public ResponseEntity<ProjectMetadataTemplate> createNewMetadataTemplate(@RequestBody CreateMetadataTemplateRequest request, @RequestParam Long projectId) {
		return ResponseEntity.ok(service.createMetadataTemplate(request, projectId));
	}

	@GetMapping("/templates/{templateId}")
	public ResponseEntity<MetadataTemplate> getMetadataTemplate(@PathVariable Long templateId) {
		return ResponseEntity.ok(service.getMetadataTemplate(templateId));
	}

	@PutMapping("/templates/{templateId}")
	public ResponseEntity<AjaxResponse> updatedMetadataTemplate(@RequestBody MetadataTemplate template) {
		service.updateMetadataTemplate(template);
		return ResponseEntity.ok(new AjaxSuccessResponse("__Template has been saved"));
	}

	@DeleteMapping("/templates/{templateId}")
	public ResponseEntity<AjaxResponse> deleteMetadataTemplate(@PathVariable Long templateId,
			@RequestParam Long projectId) {
		try {
			service.deleteMetadataTemplate(templateId, projectId);
			return ResponseEntity.ok(new AjaxSuccessResponse("__Removed template"));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new AjaxErrorResponse("Could not remove templates"));
		}
	}

	@GetMapping("/fields")
	public List<MetadataTemplateField> getMetadataFieldsForProject(@RequestParam Long projectId) {
		return service.getMetadataFieldsForProject(projectId);
	}
}
