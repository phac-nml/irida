package ca.corefacility.bioinformatics.irida.ria.web.ajax.metadata;

import java.util.List;
import java.util.Locale;

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
 * Ajax controller for project metadata templates.
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
	 *
	 * @param projectId Identifier for the project to get templates for.
	 * @return List of metadata templates with associate details.
	 */
	@GetMapping("/templates")
	public ResponseEntity<List<MetadataTemplate>> getProjectMetadataTemplates(@RequestParam Long projectId) {
		return ResponseEntity.ok(service.getProjectMetadataTemplates(projectId));
	}

	/**
	 * Create a new metadata template within a project
	 *
	 * @param request   details about the template to create
	 * @param projectId identifier for a project
	 * @return the newly created {@link ProjectMetadataTemplate}
	 */
	@PostMapping("/templates")
	public ResponseEntity<MetadataTemplate> createNewMetadataTemplate(
			@RequestBody CreateMetadataTemplateRequest request, @RequestParam Long projectId) {
		return ResponseEntity.ok(service.createMetadataTemplate(request, projectId));
	}

	/**
	 * Updated the fields in a {@link MetadataTemplate}
	 *
	 * @param template the updated template to save
	 * @param locale     Current users {@link Locale}
	 * @return Message for UI to display about the result of the update.
	 */
	@PutMapping("/templates/{templateId}")
	public ResponseEntity<AjaxResponse> updatedMetadataTemplate(@RequestBody MetadataTemplate template, Locale locale) {

		try {
			return ResponseEntity.ok(new AjaxSuccessResponse(service.updateMetadataTemplate(template, locale)));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new AjaxErrorResponse(e.getMessage()))
		}
	}

	/**
	 * Delete a metadata template from the project
	 *
	 * @param templateId Identifier for a {@link MetadataTemplate}
	 * @param projectId  Identifier for the current project
	 * @param locale     Current users {@link Locale}
	 * @return Message for UI about the result
	 */
	@DeleteMapping("/templates/{templateId}")
	public ResponseEntity<AjaxResponse> deleteMetadataTemplate(@PathVariable Long templateId,
			@RequestParam Long projectId, Locale locale) {
		try {

			return ResponseEntity.ok(
					new AjaxSuccessResponse(service.deleteMetadataTemplate(templateId, projectId, locale)));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new AjaxErrorResponse(e.getMessage()));
		}
	}

	/**
	 * Get all the metadata fields in a project
	 *
	 * @param projectId Identifier for a project
	 * @return list of {@link MetadataTemplateField}s
	 */
	@GetMapping("/fields")
	public List<MetadataTemplateField> getMetadataFieldsForProject(@RequestParam Long projectId) {
		return service.getMetadataFieldsForProject(projectId);
	}
}
