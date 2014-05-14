package ca.corefacility.bioinformatics.irida.repositories.remote.impl;

import java.net.URI;
import java.util.List;

import javax.ws.rs.core.UriBuilder;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.repositories.remote.GenericRemoteRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.model.resource.ListResourceWrapper;
import ca.corefacility.bioinformatics.irida.repositories.remote.model.resource.RemoteResource;
import ca.corefacility.bioinformatics.irida.repositories.remote.model.resource.ResourceWrapper;
import ca.corefacility.bioinformatics.irida.repositories.remote.resttemplate.OAuthTokenRestTemplate;

/**
 * Remote repository to request from remote IRIDA instances using OAuth2
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 * @param <Type>
 *            The type of object to be stored in this repository (extends
 *            {@link RemoteResource})
 */
public abstract class OltuGenericRepository<Type extends RemoteResource> implements GenericRemoteRepository<Type> {

	// the rest template to communicate with
	private OAuthTokenRestTemplate restTemplate;
	// relative URI to the resource collection
	private String relativeURI;
	// URI to the resources being stored in this repo
	private URI resourcesURI;

	// type references for the resources being read by this repository
	protected ParameterizedTypeReference<ListResourceWrapper<Type>> listTypeReference;
	protected ParameterizedTypeReference<ResourceWrapper<Type>> objectTypeReference;

	/**
	 * Create a new repository with the given rest template and object params
	 * 
	 * @param restTemplate
	 *            the {@link OAuthTokenRestTemplate} to communicate with
	 * @param relativeURI
	 *            the relative URI to the resource collection for this repo (ex:
	 *            projects)
	 * @param listTypeReference
	 *            A {@link ParameterizedTypeReference} for objects listed by the
	 *            rest template
	 * @param objectTypeReference
	 *            A {@link ParameterizedTypeReference} for individual resources
	 *            read by the rest template
	 */
	public OltuGenericRepository(OAuthTokenRestTemplate restTemplate, String relativeURI,
			ParameterizedTypeReference<ListResourceWrapper<Type>> listTypeReference,
			ParameterizedTypeReference<ResourceWrapper<Type>> objectTypeReference) {
		this.restTemplate = restTemplate;
		this.relativeURI = relativeURI;
		this.listTypeReference = listTypeReference;
		this.objectTypeReference = objectTypeReference;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Type read(Long id) {
		URI resourceIdURI = UriBuilder.fromUri(resourcesURI).path(id.toString()).build();
		ResponseEntity<ResourceWrapper<Type>> exchange = restTemplate.exchange(resourceIdURI, HttpMethod.GET,
				HttpEntity.EMPTY, objectTypeReference);

		return exchange.getBody().getResource();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Type> list() {
		ResponseEntity<ListResourceWrapper<Type>> exchange = restTemplate.exchange(resourcesURI, HttpMethod.GET,
				HttpEntity.EMPTY, listTypeReference);

		return exchange.getBody().getResource().getResources();
	}

	/**
	 * {@inheritDoc}
	 */
	public void setRemoteAPI(RemoteAPI remoteAPI) {
		restTemplate.setRemoteAPI(remoteAPI);
		URI serviceURI = remoteAPI.getServiceURI();
		resourcesURI = UriBuilder.fromUri(serviceURI).path(relativeURI).build();

	}

}
