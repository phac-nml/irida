package ca.corefacility.bioinformatics.irida.ria.web.ajax.metadata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;

@RestController
@RequestMapping("/ajax/metadata/templates")
public class MetadataTemplateAjaxController {
	private final MetadataTemplateService templateService;

	@Autowired
	public MetadataTemplateAjaxController(MetadataTemplateService templateService) {
		this.templateService = templateService;
	}

	@RequestMapping("/{templateId}")
	public ResponseEntity<MetadataTemplate> getMetadataTemplateDetails(@PathVariable Long templateId) {
		return ResponseEntity.ok(templateService.read(templateId));
	}
}
