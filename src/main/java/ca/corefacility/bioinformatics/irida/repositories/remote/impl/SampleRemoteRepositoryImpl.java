package ca.corefacility.bioinformatics.irida.repositories.remote.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import ca.corefacility.bioinformatics.irida.model.IridaRepresentationModel;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.remote.resource.ListResourceWrapper;
import ca.corefacility.bioinformatics.irida.model.remote.resource.ResourceWrapper;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.repositories.remote.SampleRemoteRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.resttemplate.OAuthTokenRestTemplate;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import ca.corefacility.bioinformatics.irida.web.controller.api.samples.RESTSampleMetadataController;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An implementation of {@link SampleRemoteRepository}
 */
@Repository
public class SampleRemoteRepositoryImpl extends RemoteRepositoryImpl<Sample> implements SampleRemoteRepository {
	private static final Logger logger = LoggerFactory.getLogger(SampleRemoteRepositoryImpl.class);

	private static final ParameterizedTypeReference<ListResourceWrapper<Sample>> listTypeReference = new ParameterizedTypeReference<ListResourceWrapper<Sample>>() {
	};
	private static final ParameterizedTypeReference<ResourceWrapper<Sample>> objectTypeReference = new ParameterizedTypeReference<ResourceWrapper<Sample>>() {
	};

	// type reference for the metadata repsonses
	private static final ParameterizedTypeReference<ResourceWrapper<SampleMetadataWrapper>> metadataTypeReference = new ParameterizedTypeReference<ResourceWrapper<SampleMetadataWrapper>>() {
	};

	private static final String METADATA_REL = RESTSampleMetadataController.METADATA_REL;

	private RemoteAPITokenService tokenService;

	@Autowired
	public SampleRemoteRepositoryImpl(RemoteAPITokenService tokenService, UserService userService) {
		super(tokenService, userService, listTypeReference, objectTypeReference);
		this.tokenService = tokenService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Sample> list(String uri, RemoteAPI remoteAPI) {
		logger.trace("Listing remote samples from " + uri);
		return super.list(uri, remoteAPI);
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, MetadataEntry> getSampleMetadata(Sample sample) {
		logger.trace("Requesting sample metadata for sample " + sample.getSelfHref());
		RemoteAPI remoteAPI = sample.getRemoteStatus().getApi();

		OAuthTokenRestTemplate restTemplate = new OAuthTokenRestTemplate(tokenService, remoteAPI);

		// get the metadata link
		Link metadataLink = sample.getLink(METADATA_REL).map(i -> i).orElse(null);

		// request metadata response
		ResponseEntity<ResourceWrapper<SampleMetadataWrapper>> exchange = restTemplate.exchange(metadataLink.getHref(),
				HttpMethod.GET, HttpEntity.EMPTY, metadataTypeReference);

		// pull metadata response from request
		Map<String, MetadataEntry> resource = exchange.getBody().getResource().getMetadata();

		return resource;
	}

	/**
	 * Class to capture the response from a sample metadata request
	 */
	private static class SampleMetadataWrapper extends IridaRepresentationModel {
		Map<String, MetadataEntry> metadata;

		@JsonProperty
		public void setMetadata(Map<String, MetadataEntry> metadata) {
			this.metadata = metadata;
		}

		@JsonProperty
		public Map<String, MetadataEntry> getMetadata() {
			return metadata;
		}
	}

}
