package ca.corefacility.bioinformatics.irida.repositories.remote.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;

import ca.corefacility.bioinformatics.irida.repositories.remote.ProjectRemoteRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.model.RemoteProject;
import ca.corefacility.bioinformatics.irida.repositories.remote.model.resource.ListResourceWrapper;
import ca.corefacility.bioinformatics.irida.repositories.remote.model.resource.ResourceWrapper;
import ca.corefacility.bioinformatics.irida.repositories.remote.resttemplate.OAuthTokenRestTemplate;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;

/**
 * Remote repository for retrieving {@link RemoteProject}s
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
@Repository
public class ProjectRemoteRepositoryImpl extends GenericRemoteRepositoryImpl<RemoteProject> implements ProjectRemoteRepository{

	public final static String relativeURI = "projects";
	
	//the type references for this repo
	private static ParameterizedTypeReference<ListResourceWrapper<RemoteProject>> listTypeReference = new ParameterizedTypeReference<ListResourceWrapper<RemoteProject>>() {};
	private static ParameterizedTypeReference<ResourceWrapper<RemoteProject>> objectTypeReference = new ParameterizedTypeReference<ResourceWrapper<RemoteProject>>() {};
	
	/**
	 * Create a new {@link ProjectRemoteRepositoryImpl} with the given rest template
	 * @param restTemplate a {@link OAuthTokenRestTemplate}
	 */
	@Autowired
	public ProjectRemoteRepositoryImpl(RemoteAPITokenService tokenService){
		super(relativeURI,tokenService,listTypeReference,objectTypeReference);
	}
}
