package ca.corefacility.bioinformatics.irida.repositories.remote.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.remote.resource.ListResourceWrapper;
import ca.corefacility.bioinformatics.irida.model.remote.resource.ResourceWrapper;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.repositories.remote.SampleRemoteRepository;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;

/**
 * An implementation of {@link SampleRemoteRepository}
 * 
 *
 */
@Repository
public class SampleRemoteRepositoryImpl extends RemoteRepositoryImpl<Sample> implements SampleRemoteRepository {
	public static final String SAMPLES_CACHE_NAME = "remoteSamplesCache";
	private static final Logger logger = LoggerFactory.getLogger(SampleRemoteRepositoryImpl.class);

	private static final ParameterizedTypeReference<ListResourceWrapper<Sample>> listTypeReference = new ParameterizedTypeReference<ListResourceWrapper<Sample>>() {
	};
	private static final ParameterizedTypeReference<ResourceWrapper<Sample>> objectTypeReference = new ParameterizedTypeReference<ResourceWrapper<Sample>>() {
	};

	@Autowired
	public SampleRemoteRepositoryImpl(RemoteAPITokenService tokenService) {
		super(tokenService, listTypeReference, objectTypeReference);
	}

	/**
	 * {@inheritDoc}
	 */
	@Cacheable(value = SAMPLES_CACHE_NAME, key = "#uri")
	@Override
	public List<Sample> list(String uri, RemoteAPI remoteAPI) {
		logger.trace("Listing remote samples from " + uri);
		return super.list(uri, remoteAPI);
	}

}
