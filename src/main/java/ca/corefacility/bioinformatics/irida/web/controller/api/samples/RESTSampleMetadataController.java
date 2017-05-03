package ca.corefacility.bioinformatics.irida.web.controller.api.samples;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

import ca.corefacility.bioinformatics.irida.model.IridaResourceSupport;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.web.controller.api.RESTGenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.RESTProjectSamplesController;

@Controller
public class RESTSampleMetadataController {
	private static final Logger logger = LoggerFactory.getLogger(RESTSampleMetadataController.class);

	public static final String SAMPLE_REL = "sample";

	private SampleService sampleService;
	private MetadataTemplateService metadataTemplateService;

	@Autowired
	public RESTSampleMetadataController(SampleService sampleService, MetadataTemplateService metadataTemplateService) {
		this.sampleService = sampleService;
		this.metadataTemplateService = metadataTemplateService;
	}

	@RequestMapping(value = "/api/samples/{sampleId}/metadata", method = RequestMethod.GET)
	public ModelMap getSampleMetadata(@PathVariable Long sampleId) {
		logger.trace("Getting sample metadata for " + sampleId);
		ModelMap modelMap = new ModelMap();
		Sample s = sampleService.read(sampleId);

		SampleMetadataResponse response = buildSampleMetadataResponse(s);

		modelMap.addAttribute(RESTGenericController.RESOURCE_NAME, response);
		return modelMap;
	}

	@RequestMapping(value = "/api/samples/{sampleId}/metadata", method = RequestMethod.POST)
	public ModelMap saveSampleMetadata(@PathVariable Long sampleId,
			@RequestBody Map<String, MetadataEntry> metadataMap) {
		Sample s = sampleService.read(sampleId);

		Map<MetadataTemplateField, MetadataEntry> metadata = metadataTemplateService.getMetadataMap(metadataMap);

		s.setMetadata(metadata);

		sampleService.update(s);

		return getSampleMetadata(sampleId);
	}

	@RequestMapping(value = "/api/samples/{sampleId}/metadata", method = RequestMethod.PUT)
	public ModelMap addSampleMetadata(@PathVariable Long sampleId,
			@RequestBody Map<String, MetadataEntry> metadataMap) {
		Sample s = sampleService.read(sampleId);

		Map<MetadataTemplateField, MetadataEntry> metadata = metadataTemplateService.getMetadataMap(metadataMap);

		s.mergeMetadata(metadata);

		sampleService.update(s);

		return getSampleMetadata(sampleId);
	}

	private SampleMetadataResponse buildSampleMetadataResponse(final Sample s) {
		SampleMetadataResponse response = new SampleMetadataResponse(s);
		response.add(linkTo(methodOn(RESTProjectSamplesController.class).getSample(s.getId())).withRel(SAMPLE_REL));

		return response;
	}

	/**
	 * Response class so we can add links to sample metadata
	 */
	private class SampleMetadataResponse extends IridaResourceSupport {
		Sample sample;

		public SampleMetadataResponse(Sample sample) {
			this.sample = sample;
		}

		@JsonAnyGetter
		public Map<MetadataTemplateField, MetadataEntry> getMetadata() {
			return sample.getMetadata();
		}
	}
}
