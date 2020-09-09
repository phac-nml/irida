package ca.corefacility.bioinformatics.irida.repositories.remote.impl;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

import ca.corefacility.bioinformatics.irida.model.IridaResourceSupport;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteStatus;
import ca.corefacility.bioinformatics.irida.model.remote.resource.ListResourceWrapper;
import ca.corefacility.bioinformatics.irida.model.remote.resource.ResourceWrapper;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.remote.RemoteRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.resttemplate.OAuthTokenRestTemplate;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;

/**
 * Remote repository to request from remote IRIDA instances using
 * {@link OAuthTokenRestTemplate}s
 * 
 *
 * @param <Type>
 *            The type of object to be stored in this repository (extends
 *            {@link IridaResourceSupport})
 */
public abstract class RemoteRepositoryImpl<Type extends IridaResourceSupport> implements RemoteRepository<Type> {

	// service storing api tokens for communication with the remote services
	private RemoteAPITokenService tokenService;

	// type references for the resources being read by this repository
	final protected ParameterizedTypeReference<ListResourceWrapper<Type>> listTypeReference;
	final protected ParameterizedTypeReference<ResourceWrapper<Type>> objectTypeReference;

	/**
	 * Create a new repository with the given rest template and object params
	 * 
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

		Type resource = exchange.getBody().getResource();

		resource = setRemoteStatus(resource, remoteAPI);
		return resource;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Type> list(String uri, RemoteAPI remoteAPI) {
		OAuthTokenRestTemplate restTemplate = new OAuthTokenRestTemplate(tokenService, remoteAPI);
		ResponseEntity<ListResourceWrapper<Type>> exchange = restTemplate.exchange(uri, HttpMethod.GET,
				HttpEntity.EMPTY, listTypeReference);

		List<Type> resources = exchange.getBody().getResource().getResources();
		for (Type r : resources) {
			r = setRemoteStatus(r, remoteAPI);
		}
		return resources;
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

	/**
	 * Set the {@link RemoteStatus} of a read remote entity
	 *
	 * @param entity The entity to set the remote status on
	 * @param api    The API to connect to
	 * @param <T>    The type of entity you're setting status of
	 * @return the enhanced entity
	 */
	protected <T extends IridaResourceSupport> T setRemoteStatus(T entity, RemoteAPI api) {
		String selfHref = entity.getSelfHref();

		// Get the logged in user and set them in the remote status object
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		RemoteStatus remoteStatus = new RemoteStatus(selfHref, api);

		if (principal instanceof User) {
			remoteStatus.setReadBy((User) principal);
		}

		remoteStatus.setRemoteHashCode(entity.hashCode());

		entity.setRemoteStatus(remoteStatus);

		return entity;
	}

}
