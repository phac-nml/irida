package ca.corefacility.bioinformatics.irida.repositories.remote.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;

import ca.corefacility.bioinformatics.irida.model.remote.RemoteSequenceFile;
import ca.corefacility.bioinformatics.irida.model.remote.resource.ListResourceWrapper;
import ca.corefacility.bioinformatics.irida.model.remote.resource.ResourceWrapper;
import ca.corefacility.bioinformatics.irida.repositories.remote.SequenceFileRemoteRepository;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;

/**
 * Implementation of {@link SequenceFileRemoteRepository}
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
@Repository
public class SequenceFileRemoteRepositoryImpl extends RemoteRepositoryImpl<RemoteSequenceFile> implements
		SequenceFileRemoteRepository {
	private static final ParameterizedTypeReference<ListResourceWrapper<RemoteSequenceFile>> listTypeReference = new ParameterizedTypeReference<ListResourceWrapper<RemoteSequenceFile>>() {
	};

	private static final ParameterizedTypeReference<ResourceWrapper<RemoteSequenceFile>> objectTypeReference = new ParameterizedTypeReference<ResourceWrapper<RemoteSequenceFile>>() {
	};

	@Autowired
	public SequenceFileRemoteRepositoryImpl(RemoteAPITokenService tokenService) {
		super(tokenService, listTypeReference, objectTypeReference);
	}

}
