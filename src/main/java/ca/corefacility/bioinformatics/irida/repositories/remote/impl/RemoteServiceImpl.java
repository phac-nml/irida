package ca.corefacility.bioinformatics.irida.repositories.remote.impl;

import java.net.URI;
import java.util.List;

import javax.ws.rs.core.UriBuilder;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.repositories.remote.RemoteService;
import ca.corefacility.bioinformatics.irida.repositories.remote.model.resource.ListResourceWrapper;
import ca.corefacility.bioinformatics.irida.repositories.remote.model.resource.RemoteResource;
import ca.corefacility.bioinformatics.irida.repositories.remote.model.resource.ResourceWrapper;
import ca.corefacility.bioinformatics.irida.repositories.remote.resttemplate.OAuthTokenRestTemplate;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;

/**
 * Remote repository to request from remote IRIDA instances using OAuth2
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 * @param <Type>
 *            The type of object to be stored in this repository (extends
 *            {@link RemoteResource})
 */
public abstract class RemoteServiceImpl<Type extends RemoteResource> implements RemoteService<Type> {

	// relative URI to the resource collection
	private String relativeURI;
	// service storing api tokens for communication with the remote services
	private RemoteAPITokenService tokenService;

	// type references for the resources being read by this repository
	final protected ParameterizedTypeReference<ListResourceWrapper<Type>> listTypeReference;
	final protected ParameterizedTypeReference<ResourceWrapper<Type>> objectTypeReference;

	/**
	 * Create a new repository with the given rest template and object params
	 * 
	 * @param restTemplate
	 *            the {@link OAuthTokenRestTemplate} to communicate with
	 * @param relativeURI
	 *            the relative URI to the resource collection for this repo (ex:
	 *            projects)
	 * @param tokenService
	 *            service storing api tokens for communication with the remote
	 *            APIs
	 * @param listTypeReference
	 *            A {@link ParameterizedTypeReference} for objects listed by the
	 *            rest template
	 * @param objectTypeReference
	 *            A {@link ParameterizedTypeReference} for individual resources
	 *            read by the rest template
	 */
	public RemoteServiceImpl(String relativeURI, RemoteAPITokenService tokenService,
			ParameterizedTypeReference<ListResourceWrapper<Type>> listTypeReference,
			ParameterizedTypeReference<ResourceWrapper<Type>> objectTypeReference) {
		this.relativeURI = relativeURI;
		this.tokenService = tokenService;
		this.listTypeReference = listTypeReference;
		this.objectTypeReference = objectTypeReference;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Type read(Long id, RemoteAPI remoteAPI) {
		URI resourceIdURI = UriBuilder.fromUri(getResourceURI(remoteAPI)).path(id.toString()).build();
		OAuthTokenRestTemplate restTemplate = new OAuthTokenRestTemplate(tokenService, remoteAPI);
		ResponseEntity<ResourceWrapper<Type>> exchange = restTemplate.exchange(resourceIdURI, HttpMethod.GET,
				HttpEntity.EMPTY, objectTypeReference);

		return exchange.getBody().getResource();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Type> list(RemoteAPI remoteAPI) {
		OAuthTokenRestTemplate restTemplate = new OAuthTokenRestTemplate(tokenService, remoteAPI);
		ResponseEntity<ListResourceWrapper<Type>> exchange = restTemplate.exchange(getResourceURI(remoteAPI),
				HttpMethod.GET, HttpEntity.EMPTY, listTypeReference);

		return exchange.getBody().getResource().getResources();
	}

	/**
	 * Get the URI for the resources you're trying to request
	 * 
	 * @param remoteAPI
	 *            the RemoteAPI we're communicating with
	 * @return the full URI for the resource type you're requesting
	 */
	private URI getResourceURI(RemoteAPI remoteAPI) {
		String serviceURI = remoteAPI.getServiceURI();
		return UriBuilder.fromUri(serviceURI).path(relativeURI).build();
	}

}
