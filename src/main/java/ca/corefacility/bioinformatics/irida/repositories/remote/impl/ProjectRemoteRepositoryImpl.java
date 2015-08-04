package ca.corefacility.bioinformatics.irida.repositories.remote.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.remote.resource.ListResourceWrapper;
import ca.corefacility.bioinformatics.irida.model.remote.resource.ResourceWrapper;
import ca.corefacility.bioinformatics.irida.repositories.remote.ProjectRemoteRepository;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;

/**
 * Remote repository for retrieving {@link Project}s from {@link RemoteAPI}s
 * 
 *
 */
@Repository
public class ProjectRemoteRepositoryImpl extends RemoteRepositoryImpl<Project> implements ProjectRemoteRepository {

	// the type references for this repo
	private static final ParameterizedTypeReference<ListResourceWrapper<Project>> listTypeReference = new ParameterizedTypeReference<ListResourceWrapper<Project>>() {
	};
	private static final ParameterizedTypeReference<ResourceWrapper<Project>> objectTypeReference = new ParameterizedTypeReference<ResourceWrapper<Project>>() {
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
