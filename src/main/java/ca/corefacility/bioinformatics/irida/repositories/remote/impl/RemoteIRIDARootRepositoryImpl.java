package ca.corefacility.bioinformatics.irida.repositories.remote.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteIRIDARoot;
import ca.corefacility.bioinformatics.irida.model.remote.resource.ListResourceWrapper;
import ca.corefacility.bioinformatics.irida.model.remote.resource.ResourceWrapper;
import ca.corefacility.bioinformatics.irida.repositories.remote.RemoteIRIDARootRepository;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;

/**
 * Implementation of {@link RemoteIRIDARootRepository}
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
@Repository
public class RemoteIRIDARootRepositoryImpl extends RemoteRepositoryImpl<RemoteIRIDARoot> implements
		RemoteIRIDARootRepository {
	private static final ParameterizedTypeReference<ListResourceWrapper<RemoteIRIDARoot>> listTypeReference = new ParameterizedTypeReference<ListResourceWrapper<RemoteIRIDARoot>>() {
	};

	private static final ParameterizedTypeReference<ResourceWrapper<RemoteIRIDARoot>> objectTypeReference = new ParameterizedTypeReference<ResourceWrapper<RemoteIRIDARoot>>() {
	};

	@Autowired
	public RemoteIRIDARootRepositoryImpl(RemoteAPITokenService tokenService) {
		super(tokenService, listTypeReference, objectTypeReference);
	}

	/**
	 * {@inheritDoc}
	 */
	public RemoteIRIDARoot read(RemoteAPI api) {
		return read(api.getServiceURI(), api);
	}

}
