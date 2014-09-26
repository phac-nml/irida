package ca.corefacility.bioinformatics.irida.service.remote.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteRelatedProject;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectRemoteService;
import ca.corefacility.bioinformatics.irida.service.remote.model.RemoteProject;
import ca.corefacility.bioinformatics.irida.service.remote.model.resource.ListResourceWrapper;
import ca.corefacility.bioinformatics.irida.service.remote.model.resource.ResourceWrapper;
import ca.corefacility.bioinformatics.irida.service.remote.resttemplate.OAuthTokenRestTemplate;

/**
 * Remote service for retrieving {@link RemoteProject}s
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
@Service
public class ProjectRemoteServiceImpl extends RemoteServiceImpl<RemoteProject> implements ProjectRemoteService {

	// the type references for this repo
	private static ParameterizedTypeReference<ListResourceWrapper<RemoteProject>> listTypeReference = new ParameterizedTypeReference<ListResourceWrapper<RemoteProject>>() {
	};
	private static ParameterizedTypeReference<ResourceWrapper<RemoteProject>> objectTypeReference = new ParameterizedTypeReference<ResourceWrapper<RemoteProject>>() {
	};

	/**
	 * Create a new {@link ProjectRemoteServiceImpl} with the given rest
	 * template
	 * 
	 * @param restTemplate
	 *            a {@link OAuthTokenRestTemplate}
	 */
	@Autowired
	public ProjectRemoteServiceImpl(RemoteAPITokenService tokenService) {
		super(tokenService, listTypeReference, objectTypeReference);
	}

	/**
	 * Read a {@link RemoteProject} for a given {@link RemoteRelatedProject}
	 * reference
	 * 
	 * @param project
	 *            The {@link RemoteRelatedProject} to read
	 * @return a {@link RemoteProject}
	 */
	public RemoteProject read(RemoteRelatedProject project) {
		String remoteProjectURI = project.getRemoteProjectURI();
		RemoteAPI remoteAPI = project.getRemoteAPI();

		return read(remoteProjectURI, remoteAPI);
	}
}
