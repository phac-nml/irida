package ca.corefacility.bioinformatics.irida.repositories.remote.oltu;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.ws.rs.core.UriBuilder;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import ca.corefacility.bioinformatics.irida.repositories.remote.ProjectRemoteRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.model.ListResourceWrapper;
import ca.corefacility.bioinformatics.irida.repositories.remote.model.RemoteProject;
import ca.corefacility.bioinformatics.irida.repositories.remote.model.ResourceWrapper;

public class OltuProjectRemoteRepository{// implements ProjectRemoteRepository{

	private OAuthTokenRestTemplate restTemplate;
	private URI resourcesURI;
	private URI baseURI;
	private String relativeURI = "projects";
	
	ParameterizedTypeReference<ListResourceWrapper<RemoteProject>> listTypeReference;
	ParameterizedTypeReference<ResourceWrapper<RemoteProject>> objectTypeReference;
	
	
	public OltuProjectRemoteRepository(OAuthTokenRestTemplate restTemplate){
		this.restTemplate = restTemplate;
		
		listTypeReference = new ParameterizedTypeReference<ListResourceWrapper<RemoteProject>>() {};
		objectTypeReference = new ParameterizedTypeReference<ResourceWrapper<RemoteProject>>() {};
	}
	
	//@Override
	public RemoteProject read(Long id) {
		URI resourceIdURI = UriBuilder.fromUri(resourcesURI).path(id.toString()).build();
		ResponseEntity<ResourceWrapper<RemoteProject>> exchange = restTemplate.exchange(resourceIdURI, HttpMethod.GET, HttpEntity.EMPTY, objectTypeReference);

		return exchange.getBody().getResource();
	}

	//@Override
	public List<RemoteProject> list() throws IOException{
		ResponseEntity<ListResourceWrapper<RemoteProject>> exchange = restTemplate.exchange(resourcesURI, HttpMethod.GET, HttpEntity.EMPTY, listTypeReference);
		ListResourceWrapper<RemoteProject> body = exchange.getBody();
		
		return body.getResource().getResources();
	}

	//@Override
	public void setBaseURI(URI baseURI) {
		this.baseURI = baseURI;
		restTemplate.setServiceURI(baseURI);
		resourcesURI = UriBuilder.fromUri(baseURI).path(relativeURI).build();
	}
}
