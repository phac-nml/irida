package ca.corefacility.bioinformatics.irida.ria.web.cart.dto;

import java.util.List;
import java.util.Map;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.RESTProjectSamplesController;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

public class GalaxyExportSample {
	private String name;
	private Map<String, Map<String, String>> _links;
	private Map<String, List<Map<String, Map<String, Map<String, String>>>>> _embedded;

	public GalaxyExportSample(Sample sample, Long projectId) {
		this.name = sample.getSampleName();
		/*
		This is expected by Galaxy.
		 */
		this._links = ImmutableMap.of("self", ImmutableMap.of("href", ""));

		Map<String, String> href = ImmutableMap.of("href",
				linkTo(methodOn(RESTProjectSamplesController.class).getProjectSample(projectId,
						sample.getId())).withSelfRel()
						.getHref());
		Map<String, Map<String, String>> self = ImmutableMap.of("self", href);
		Map<String, Map<String, Map<String, String>>> _links = ImmutableMap.of("_links", self);
		List<Map<String, Map<String, Map<String, String>>>> sampleFiles = ImmutableList.of(_links);
		this._embedded = ImmutableMap.of("sample_files", sampleFiles);
	}

	public String getName() {
		return name;
	}

	public Map<String, Map<String, String>> get_links() {
		return _links;
	}

	public Map<String, List<Map<String, Map<String, Map<String, String>>>>> get_embedded() {
		return _embedded;
	}
}
