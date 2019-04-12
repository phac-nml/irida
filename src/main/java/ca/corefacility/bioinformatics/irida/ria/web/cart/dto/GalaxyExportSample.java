package ca.corefacility.bioinformatics.irida.ria.web.cart.dto;

import java.util.List;
import java.util.Map;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.RESTProjectSamplesController;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * UI Model to return sample links in the format that galaxy expects:
 *
 * <pre>
 *     [{
 *         "name" : "sample_name",
 *         "_links" : { "self" : { "href" : ""} },
 *         "_embedded" : {
 *             "sample_files" : [
 *                {
 *             	    "_links" : {
 *             	        "self" : {
 *             	            "href" : "http://samples_href"
 *                        }
 *                    }
 *                }, ...
 *             ]
 *         }
 *     }]
 * </pre>
 */
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

	/**
	 * Get the {@link Map} for this sample
	 *
	 * @return {@link Map} of links
	 */
	@JsonProperty("_links")
	public Map<String, Map<String, String>> getLinks() {
		return _links;
	}

	/**
	 * Get a {@link Map} of all the file links
	 *
	 * @return {@link Map} of file links
	 */
	@JsonProperty("_embedded")
	public Map<String, List<Map<String, Map<String, Map<String, String>>>>> getEmbedded() {
		return _embedded;
	}
}
