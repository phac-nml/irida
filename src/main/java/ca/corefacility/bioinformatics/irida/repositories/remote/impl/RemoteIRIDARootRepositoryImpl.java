package ca.corefacility.bioinformatics.irida.repositories.remote.impl;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;

import ca.corefacility.bioinformatics.irida.model.remote.RemoteIRIDARoot;
import ca.corefacility.bioinformatics.irida.model.remote.resource.ListResourceWrapper;
import ca.corefacility.bioinformatics.irida.model.remote.resource.ResourceWrapper;
import ca.corefacility.bioinformatics.irida.repositories.remote.RemoteIRIDARootRepository;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;

@Repository
public class RemoteIRIDARootRepositoryImpl extends RemoteRepositoryImpl<RemoteIRIDARoot> implements
		RemoteIRIDARootRepository {
	private static final ParameterizedTypeReference<ListResourceWrapper<RemoteIRIDARoot>> listTypeReference = new ParameterizedTypeReference<ListResourceWrapper<RemoteIRIDARoot>>() {
	};

	private static final ParameterizedTypeReference<ResourceWrapper<RemoteIRIDARoot>> objectTypeReference = new ParameterizedTypeReference<ResourceWrapper<RemoteIRIDARoot>>() {
	};

	public RemoteIRIDARootRepositoryImpl(RemoteAPITokenService tokenService) {
		super(tokenService, listTypeReference, objectTypeReference);
	}

}
