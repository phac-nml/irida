package ca.corefacility.bioinformatics.irida.repositories.remote.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteSample;
import ca.corefacility.bioinformatics.irida.model.remote.resource.ListResourceWrapper;
import ca.corefacility.bioinformatics.irida.model.remote.resource.ResourceWrapper;
import ca.corefacility.bioinformatics.irida.repositories.remote.SampleRemoteRepository;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;

/**
 * An implementation of {@link SampleRemoteRepository}
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
@Repository
public class SampleRemoteRepositoryImpl extends RemoteRepositoryImpl<RemoteSample> implements SampleRemoteRepository {
	public static final String SAMPLES_CACHE_NAME = "remoteSamplesCache";
	public static final int SAMPLES_CACHE_EXPIRY = 60;
	private static final Logger logger = LoggerFactory.getLogger(SampleRemoteRepositoryImpl.class);

	private static final ParameterizedTypeReference<ListResourceWrapper<RemoteSample>> listTypeReference = new ParameterizedTypeReference<ListResourceWrapper<RemoteSample>>() {
	};
	private static final ParameterizedTypeReference<ResourceWrapper<RemoteSample>> objectTypeReference = new ParameterizedTypeReference<ResourceWrapper<RemoteSample>>() {
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
	public List<RemoteSample> list(String uri, RemoteAPI remoteAPI) {
		logger.trace("Listing remote samples from " + uri);
		return super.list(uri, remoteAPI);
	}

}
