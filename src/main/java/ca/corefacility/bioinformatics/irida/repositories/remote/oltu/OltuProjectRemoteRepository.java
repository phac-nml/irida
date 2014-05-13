package ca.corefacility.bioinformatics.irida.repositories.remote.oltu;

import org.springframework.core.ParameterizedTypeReference;

import ca.corefacility.bioinformatics.irida.repositories.remote.ProjectRemoteRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.model.RemoteProject;
import ca.corefacility.bioinformatics.irida.repositories.remote.model.resource.ListResourceWrapper;
import ca.corefacility.bioinformatics.irida.repositories.remote.model.resource.ResourceWrapper;

public class OltuProjectRemoteRepository extends OltuGenericRepository<RemoteProject> implements ProjectRemoteRepository{

	public final static String relativeURI = "projects";
	
	private static ParameterizedTypeReference<ListResourceWrapper<RemoteProject>> listTypeReference = new ParameterizedTypeReference<ListResourceWrapper<RemoteProject>>() {};
	private static ParameterizedTypeReference<ResourceWrapper<RemoteProject>> objectTypeReference = new ParameterizedTypeReference<ResourceWrapper<RemoteProject>>() {};
	
	
	public OltuProjectRemoteRepository(OAuthTokenRestTemplate restTemplate){
		super(restTemplate,relativeURI,listTypeReference,objectTypeReference);
	}
}
