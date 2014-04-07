package ca.corefacility.bioinformatics.irida.repositories.remote.impl;

import java.net.URI;
import java.util.List;

import javax.ws.rs.core.UriBuilder;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;

import ca.corefacility.bioinformatics.irida.repositories.remote.GenericRemoteRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.model.ListResourceWrapper;
import ca.corefacility.bioinformatics.irida.repositories.remote.model.RemoteResource;
import ca.corefacility.bioinformatics.irida.repositories.remote.model.ResourceWrapper;

public class GenericRemoteRepositoryImpl<Type extends RemoteResource> implements GenericRemoteRepository<Type>{
	private OAuth2RestTemplate restTemplate;
	
	private URI baseURI;
	private String relativeURI;
	private URI resourcesURI;
	ParameterizedTypeReference<ListResourceWrapper<Type>> listTypeReference;
	ParameterizedTypeReference<ResourceWrapper<Type>> objectTypeReference;
	
	public GenericRemoteRepositoryImpl(OAuth2RestTemplate restTemplate, String relativeURI, ParameterizedTypeReference<ResourceWrapper<Type>> objectTypeReference, 
			ParameterizedTypeReference<ListResourceWrapper<Type>> listTypeReference ){
		this.restTemplate = restTemplate;
		this.relativeURI = relativeURI;
		this.listTypeReference = listTypeReference;
		this.objectTypeReference = objectTypeReference;
	}
	
	@Override
	public Type read(Long id) {
		URI resourceIdURI = UriBuilder.fromUri(resourcesURI).path(id.toString()).build();
		ResponseEntity<ResourceWrapper<Type>> exchange = restTemplate.exchange(resourceIdURI, HttpMethod.GET, HttpEntity.EMPTY, objectTypeReference);

		return exchange.getBody().getResource();
	}

	@Override
	public List<Type> list() {
		ResponseEntity<ListResourceWrapper<Type>> exchange = restTemplate.exchange(resourcesURI, HttpMethod.GET, HttpEntity.EMPTY, listTypeReference);
		ListResourceWrapper<Type> body = exchange.getBody();
		
		return body.getResource().getResources();
	}

	@Override
	public void setBaseURI(URI baseURI) {
		this.baseURI = baseURI;
		resourcesURI = UriBuilder.fromUri(baseURI).path(relativeURI).build();
	}

}
