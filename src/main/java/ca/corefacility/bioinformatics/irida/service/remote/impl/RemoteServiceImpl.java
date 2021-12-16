package ca.corefacility.bioinformatics.irida.service.remote.impl;

import java.util.List;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.IridaRepresentationModel;
import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.repositories.RemoteAPIRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.RemoteRepository;
import ca.corefacility.bioinformatics.irida.service.remote.RemoteService;

/**
 * Remote service to request from remote IRIDA instances using OAuth2
 * 
 *
 * @param <Type>
 *            The type of object to be stored in this repository (extends
 *            {@link IridaRepresentationModel})
 */
public abstract class RemoteServiceImpl<Type extends IridaRepresentationModel & IridaThing> implements RemoteService<Type> {

	private final RemoteRepository<Type> repository;
	private final RemoteAPIRepository remoteAPIRepository;

	/**
	 * Create a new remote service that interacts with the given repository
	 * 
	 * @param repository
	 *            The {@link RemoteRepository} handling basic operations with
	 *            the given Type
	 * @param remoteAPIRepository
	 *            repository for storing and retrieving {@link RemoteAPI}s
	 */
	public RemoteServiceImpl(RemoteRepository<Type> repository, RemoteAPIRepository remoteAPIRepository) {
		this.repository = repository;
		this.remoteAPIRepository = remoteAPIRepository;
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
	public Type read(String uri) {
		RemoteAPI remoteAPIForUrl = remoteAPIRepository.getRemoteAPIForUrl(uri);

		if (remoteAPIForUrl == null) {
			throw new EntityNotFoundException("RemoteAPI could not be found for this URL: " + uri);
		}

		return read(uri, remoteAPIForUrl);
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

	/**
	 * Convenience method for getting the {@link RemoteAPI} for a given string
	 * URI
	 * 
	 * @param uri
	 *            the URI to get a {@link RemoteAPI} for
	 * @return the {@link RemoteAPI}
	 */
	protected RemoteAPI getRemoteApiForURI(String uri) {
		return remoteAPIRepository.getRemoteAPIForUrl(uri);
	}

}
