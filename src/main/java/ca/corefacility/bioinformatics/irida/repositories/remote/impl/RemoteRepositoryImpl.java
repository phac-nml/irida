package ca.corefacility.bioinformatics.irida.repositories.remote.impl;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.remote.resource.ListResourceWrapper;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RemoteResource;
import ca.corefacility.bioinformatics.irida.model.remote.resource.ResourceWrapper;
import ca.corefacility.bioinformatics.irida.repositories.remote.RemoteRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.resttemplate.OAuthTokenRestTemplate;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;

/**
 * Remote repository to request from remote IRIDA instances using
 * {@link OAuthTokenRestTemplate}s
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 * @param <Type>
 *            The type of object to be stored in this repository (extends
 *            {@link RemoteResource})
 */
public abstract class RemoteRepositoryImpl<Type extends RemoteResource> implements RemoteRepository<Type> {

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
	public RemoteRepositoryImpl(RemoteAPITokenService tokenService,
			ParameterizedTypeReference<ListResourceWrapper<Type>> listTypeReference,
			ParameterizedTypeReference<ResourceWrapper<Type>> objectTypeReference) {
		this.tokenService = tokenService;
		this.listTypeReference = listTypeReference;
		this.objectTypeReference = objectTypeReference;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Type read(String uri, RemoteAPI remoteAPI) {
		OAuthTokenRestTemplate restTemplate = new OAuthTokenRestTemplate(tokenService, remoteAPI);
		ResponseEntity<ResourceWrapper<Type>> exchange = restTemplate.exchange(uri, HttpMethod.GET, HttpEntity.EMPTY,
				objectTypeReference);

		return exchange.getBody().getResource();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Type> list(String uri, RemoteAPI remoteAPI) {
		OAuthTokenRestTemplate restTemplate = new OAuthTokenRestTemplate(tokenService, remoteAPI);
		ResponseEntity<ListResourceWrapper<Type>> exchange = restTemplate.exchange(uri, HttpMethod.GET,
				HttpEntity.EMPTY, listTypeReference);

		return exchange.getBody().getResource().getResources();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean getServiceStatus(RemoteAPI remoteAPI) {
		OAuthTokenRestTemplate restTemplate = new OAuthTokenRestTemplate(tokenService, remoteAPI);
		ResponseEntity<String> forEntity = restTemplate.getForEntity(remoteAPI.getServiceURI(), String.class);

		return forEntity.getStatusCode() == HttpStatus.OK;
	}

}
