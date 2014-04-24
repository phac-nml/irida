package ca.corefacility.bioinformatics.irida.repositories.remote.oltu;

import java.net.URI;
import java.util.List;

import javax.ws.rs.core.UriBuilder;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.repositories.remote.GenericRemoteRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.model.ListResourceWrapper;
import ca.corefacility.bioinformatics.irida.repositories.remote.model.RemoteResource;
import ca.corefacility.bioinformatics.irida.repositories.remote.model.ResourceWrapper;

public abstract class OltuGenericRepository<Type extends RemoteResource> implements GenericRemoteRepository<Type> {

	private OAuthTokenRestTemplate restTemplate;
	private String relativeURI;
	private URI resourcesURI;

	ParameterizedTypeReference<ListResourceWrapper<Type>> listTypeReference;
	ParameterizedTypeReference<ResourceWrapper<Type>> objectTypeReference;

	public OltuGenericRepository(OAuthTokenRestTemplate restTemplate,
			String relativeURI,
			ParameterizedTypeReference<ListResourceWrapper<Type>> listTypeReference,
			ParameterizedTypeReference<ResourceWrapper<Type>> objectTypeReference) {
		this.restTemplate = restTemplate;
		this.relativeURI = relativeURI;
		this.listTypeReference = listTypeReference;
		this.objectTypeReference = objectTypeReference;
	}

	@Override
	public Type read(Long id) {
		URI resourceIdURI = UriBuilder.fromUri(resourcesURI).path(id.toString()).build();
		ResponseEntity<ResourceWrapper<Type>> exchange = restTemplate.exchange(resourceIdURI, HttpMethod.GET,
				HttpEntity.EMPTY, objectTypeReference);

		return exchange.getBody().getResource();
	}

	@Override
	public List<Type> list() {
		ResponseEntity<ListResourceWrapper<Type>> exchange = restTemplate.exchange(resourcesURI, HttpMethod.GET,
				HttpEntity.EMPTY, listTypeReference);

		return exchange.getBody().getResource().getResources();
	}

	public OAuthTokenRestTemplate getRestTemplate() {
		return restTemplate;
	}
	
	public void setRemoteAPI(RemoteAPI remoteAPI){
		restTemplate.setRemoteAPI(remoteAPI);
		URI serviceURI = remoteAPI.getServiceURI();
		resourcesURI = UriBuilder.fromUri(serviceURI).path(relativeURI).build();

	}

}
