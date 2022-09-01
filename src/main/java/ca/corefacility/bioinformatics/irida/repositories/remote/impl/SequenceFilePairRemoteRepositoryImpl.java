package ca.corefacility.bioinformatics.irida.repositories.remote.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;

import ca.corefacility.bioinformatics.irida.model.IridaRepresentationModel;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.remote.resource.ListResourceWrapper;
import ca.corefacility.bioinformatics.irida.model.remote.resource.ResourceWrapper;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.repositories.remote.SequenceFilePairRemoteRepository;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

/**
 * {@link RemoteRepositoryImpl} for reading and listing {@link SequenceFilePair} objects.
 */
@Repository
public class SequenceFilePairRemoteRepositoryImpl extends RemoteRepositoryImpl<SequenceFilePair>
		implements SequenceFilePairRemoteRepository {
	private static final ParameterizedTypeReference<ListResourceWrapper<SequenceFilePair>> listTypeReference = new ParameterizedTypeReference<ListResourceWrapper<SequenceFilePair>>() {
	};
	private static final ParameterizedTypeReference<ResourceWrapper<SequenceFilePair>> objectTypeReference = new ParameterizedTypeReference<ResourceWrapper<SequenceFilePair>>() {
	};

	@Autowired
	public SequenceFilePairRemoteRepositoryImpl(RemoteAPITokenService tokenService, UserService userService) {
		super(tokenService, userService, listTypeReference, objectTypeReference);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected <T extends IridaRepresentationModel> T setRemoteStatus(T entity, RemoteAPI api) {
		entity = super.setRemoteStatus(entity, api);

		SequenceFilePair pair = (SequenceFilePair) entity;

		pair.getFiles().forEach(f -> super.setRemoteStatus(f, api));

		return entity;
	}
}
