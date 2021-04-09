package ca.corefacility.bioinformatics.irida.web.controller.api.samples;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResponseResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import ca.corefacility.bioinformatics.irida.model.IridaResourceSupport;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.RESTProjectSamplesController;

/**
 * REST controller to handle storing and retrieving metadata from a
 * {@link Sample}
 */
@Tag(name = "samples")
@Controller
public class RESTSampleMetadataController {
	private static final Logger logger = LoggerFactory.getLogger(RESTSampleMetadataController.class);

	public static final String METADATA_REL = "sample/metadata";
	public static final String SAMPLE_REL = "sample";

	private SampleService sampleService;
	private MetadataTemplateService metadataTemplateService;

	@Autowired
	public RESTSampleMetadataController(SampleService sampleService, MetadataTemplateService metadataTemplateService) {
		this.sampleService = sampleService;
		this.metadataTemplateService = metadataTemplateService;
	}

	/**
	 * Get the metadata for a given {@link Sample}
	 * 
	 * @param sampleId
	 *            the id of the {@link Sample} to get metadata for
	 * @return the metadata for the sample
	 */
	@Operation(operationId = "getSampleMetadata", summary = "Find the metadata for a given sample",
			description = "Get the metadata for a given sample.", tags = "samples")
	@RequestMapping(value = "/api/samples/{sampleId}/metadata", method = RequestMethod.GET)
	@ResponseBody
	public ResponseResource<SampleMetadataResponse> getSampleMetadata(@PathVariable Long sampleId) {
		logger.trace("Getting sample metadata for " + sampleId);
		Sample s = sampleService.read(sampleId);

		Set<MetadataEntry> metadataForSample = sampleService.getMetadataForSample(s);

		SampleMetadataResponse response = buildSampleMetadataResponse(s, metadataForSample);

		ResponseResource<SampleMetadataResponse> responseObject = new ResponseResource<>(response);
		return responseObject;
	}

	/**
	 * Save new metadata for a {@link Sample}. Note this will overwrite the
	 * existing metadata
	 * 
	 * @param sampleId
	 *            the id of the {@link Sample} to save new metadata
	 * @param metadataMap
	 *            the metadata to save to the {@link Sample}
	 * @return the updated {@link Sample}
	 */
	@Operation(operationId = "saveSampleMetadata", summary = "Save the metadata for a given sample",
			description = "Save the metadata for a given sample.", tags = "samples")
	@RequestMapping(value = "/api/samples/{sampleId}/metadata", method = RequestMethod.POST)
	@ResponseBody
	public ResponseResource<SampleMetadataResponse> saveSampleMetadata(@PathVariable Long sampleId, @RequestBody Map<String, MetadataEntry> metadataMap) {
		Sample s = sampleService.read(sampleId);

		Set<MetadataEntry> metadata = metadataTemplateService.convertMetadataStringsToSet(metadataMap);

		sampleService.updateSampleMetadata(s,metadata);

		return getSampleMetadata(sampleId);
	}

	/**
	 * Add select new metadata fields to the {@link Sample}. Note this will only
	 * overwrite duplicate terms. Existing metadata will not be affected.
	 * 
	 * @param sampleId
	 *            the {@link Sample} to add metadata to
	 * @param metadataMap
	 *            the new metadata
	 * @return the updated {@link Sample}
	 */
	@Operation(operationId = "addSampleMetadata", summary = "Add new metadata fields for a given sample",
			description = "Add new metadata fields for a given sample.", tags = "samples")
	@RequestMapping(value = "/api/samples/{sampleId}/metadata", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseResource<SampleMetadataResponse> addSampleMetadata(@PathVariable Long sampleId,
			@RequestBody Map<String, MetadataEntry> metadataMap) {
		Sample s = sampleService.read(sampleId);

		Set<MetadataEntry> metadata = metadataTemplateService.convertMetadataStringsToSet(metadataMap);
		
		sampleService.mergeSampleMetadata(s, metadata);

		return getSampleMetadata(sampleId);
	}

	/**
	 * Build a {@link SampleMetadataResponse} object
	 * 
	 * @param s
	 *            the {@link Sample} to build the object from
	 * @return a constructed {@link SampleMetadataResponse}
	 */
	private SampleMetadataResponse buildSampleMetadataResponse(final Sample s, Set<MetadataEntry> metadataEntries) {
		SampleMetadataResponse response = new SampleMetadataResponse(metadataEntries);
		response.add(linkTo(methodOn(RESTSampleMetadataController.class).getSampleMetadata(s.getId())).withSelfRel());
		response.add(linkTo(methodOn(RESTProjectSamplesController.class).getSample(s.getId())).withRel(SAMPLE_REL));
		return response;
	}

	/**
	 * Response class so we can add links to sample metadata
	 */
	private class SampleMetadataResponse extends IridaResourceSupport {
		Map<MetadataTemplateField, MetadataEntry> metadata;

		@Deprecated
		public SampleMetadataResponse(Map<MetadataTemplateField, MetadataEntry> metadata) {
			this.metadata = metadata;
		}

		public SampleMetadataResponse(Set<MetadataEntry> metadataEntrySet) {
			metadata = new HashMap<>();
			for (MetadataEntry entry : metadataEntrySet) {
				metadata.put(entry.getField(), entry);
			}
		}

		@JsonProperty
		public Map<MetadataTemplateField, MetadataEntry> getMetadata() {
			return metadata;
		}

		@JsonProperty
		public void setMetadata(Map<MetadataTemplateField, MetadataEntry> metadata) {
			this.metadata = metadata;
		}
	}
}
