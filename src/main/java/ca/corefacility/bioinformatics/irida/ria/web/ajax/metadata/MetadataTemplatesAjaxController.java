package ca.corefacility.bioinformatics.irida.ria.web.ajax.metadata;

import java.util.List;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
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

	@RequestMapping(value = "/{templateId}", method = RequestMethod.GET)
	public ResponseEntity<MetadataTemplate> getMetadataTemplateDetails(@PathVariable Long templateId) {
		return ResponseEntity.ok(service.getMetadataTemplateDetails(templateId));
	}

	@RequestMapping(value = "/{templateId}", method = RequestMethod.PUT)
	public ResponseEntity<String> updateTemplateAttribute(@PathVariable Long templateId, @RequestParam String field,
			@RequestParam String value) {
		try {
			return ResponseEntity.ok(service.updateTemplateAttribute(templateId, field, value));
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(e.getMessage());
		} catch (ConstraintViolationException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(e.getMessage());
		}
	}
}
