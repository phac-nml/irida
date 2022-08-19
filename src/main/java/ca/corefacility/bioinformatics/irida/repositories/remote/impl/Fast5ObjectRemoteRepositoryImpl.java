package ca.corefacility.bioinformatics.irida.repositories.remote.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;

import ca.corefacility.bioinformatics.irida.model.IridaRepresentationModel;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.remote.resource.ListResourceWrapper;
import ca.corefacility.bioinformatics.irida.model.remote.resource.ResourceWrapper;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.Fast5Object;
import ca.corefacility.bioinformatics.irida.repositories.remote.Fast5ObjectRemoteRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.resttemplate.OAuthTokenRestTemplate;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

/**
 * A repository implementaion for reading {@link Fast5Object} from remote locations using a
 * {@link OAuthTokenRestTemplate}
 */
@Repository
public class Fast5ObjectRemoteRepositoryImpl extends RemoteRepositoryImpl<Fast5Object>
		implements Fast5ObjectRemoteRepository {

	public static final MediaType DEFAULT_DOWNLOAD_MEDIA_TYPE = new MediaType("application", "fast5");
	private static final ParameterizedTypeReference<ListResourceWrapper<Fast5Object>> listTypeReference = new ParameterizedTypeReference<ListResourceWrapper<Fast5Object>>() {
	};
	private static final ParameterizedTypeReference<ResourceWrapper<Fast5Object>> objectTypeReference = new ParameterizedTypeReference<ResourceWrapper<Fast5Object>>() {
	};

	@Autowired
	public Fast5ObjectRemoteRepositoryImpl(RemoteAPITokenService tokenService, UserService userService) {
		super(tokenService, userService, listTypeReference, objectTypeReference);
	}

	@Override
	protected <T extends IridaRepresentationModel> T setRemoteStatus(T entity, RemoteAPI api) {
		entity = super.setRemoteStatus(entity, api);

		Fast5Object fast5Object = (Fast5Object) entity;

		super.setRemoteStatus(fast5Object.getFile(), api);

		return entity;
	}
}
