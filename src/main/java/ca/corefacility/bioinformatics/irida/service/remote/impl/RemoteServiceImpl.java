package ca.corefacility.bioinformatics.irida.service.remote.impl;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RemoteResource;
import ca.corefacility.bioinformatics.irida.repositories.remote.RemoteRepository;
import ca.corefacility.bioinformatics.irida.service.remote.RemoteService;

/**
 * Remote service to request from remote IRIDA instances using OAuth2
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 * @param <Type>
 *            The type of object to be stored in this repository (extends
 *            {@link RemoteResource})
 */
public abstract class RemoteServiceImpl<Type extends RemoteResource> implements RemoteService<Type> {

	private final RemoteRepository<Type> repository;

	/**
	 * Create a new remote service that interacts with the given repository
	 * 
	 * @param repository
	 *            The {@link RemoteRepository} handling basic operations with
	 *            the given Type
	 */
	public RemoteServiceImpl(RemoteRepository<Type> repository) {
		this.repository = repository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Type read(String uri, RemoteAPI remoteAPI) {
		return repository.read(uri, remoteAPI);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Type> list(String uri, RemoteAPI remoteAPI) {
		return repository.list(uri, remoteAPI);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean getServiceStatus(RemoteAPI remoteAPI) {
		return repository.getServiceStatus(remoteAPI);
	}

}
