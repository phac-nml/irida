package ca.corefacility.bioinformatics.irida.repositories.remote.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;

import ca.corefacility.bioinformatics.irida.model.remote.RemoteProject;
import ca.corefacility.bioinformatics.irida.model.remote.resource.ListResourceWrapper;
import ca.corefacility.bioinformatics.irida.model.remote.resource.ResourceWrapper;
import ca.corefacility.bioinformatics.irida.repositories.remote.ProjectRemoteRepository;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;

/**
 * Remote repository for retrieving {@link RemoteProject}s
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
@Repository
public class ProjectRemoteRepositoryImpl extends RemoteRepositoryImpl<RemoteProject> implements ProjectRemoteRepository {

	// the type references for this repo
	private static ParameterizedTypeReference<ListResourceWrapper<RemoteProject>> listTypeReference = new ParameterizedTypeReference<ListResourceWrapper<RemoteProject>>() {
	};
	private static ParameterizedTypeReference<ResourceWrapper<RemoteProject>> objectTypeReference = new ParameterizedTypeReference<ResourceWrapper<RemoteProject>>() {
	};

	/**
	 * Create a new {@link ProjectRemoteRepositoryImpl} with the given
	 * {@link RemoteAPITokenService}
	 * 
	 * @param tokenService
	 *            the {@link RemoteAPITokenService}
	 */
	@Autowired
	public ProjectRemoteRepositoryImpl(RemoteAPITokenService tokenService) {
		super(tokenService, listTypeReference, objectTypeReference);
	}

}
