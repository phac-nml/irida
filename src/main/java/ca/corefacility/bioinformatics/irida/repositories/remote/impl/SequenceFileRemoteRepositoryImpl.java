package ca.corefacility.bioinformatics.irida.repositories.remote.impl;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.core.token.TokenService;
import org.springframework.stereotype.Repository;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.remote.resource.ListResourceWrapper;
import ca.corefacility.bioinformatics.irida.model.remote.resource.ResourceWrapper;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.repositories.remote.SequenceFileRemoteRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.resttemplate.OAuthTokenRestTemplate;
import ca.corefacility.bioinformatics.irida.repositories.remote.resttemplate.SequenceFileMessageConverter;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

/**
 * Implementation of {@link SequenceFileRemoteRepository} using {@link OAuthTokenRestTemplate} for making requests
 */
@Repository
public class SequenceFileRemoteRepositoryImpl extends RemoteRepositoryImpl<SequenceFile>
		implements SequenceFileRemoteRepository {
	public static final MediaType DEFAULT_DOWNLOAD_MEDIA_TYPE = new MediaType("application", "fastq");
	private static final ParameterizedTypeReference<ListResourceWrapper<SequenceFile>> listTypeReference = new ParameterizedTypeReference<>() {
	};
	private static final ParameterizedTypeReference<ResourceWrapper<SequenceFile>> objectTypeReference = new ParameterizedTypeReference<>() {
	};
	// OAuth2 token storage service for making requests
	private final RemoteAPITokenService tokenService;

	/**
	 * Create a new SequenceFileRemoteRepositoryImpl
	 * 
	 * @param tokenService The {@link TokenService} storing OAuth2 tokens
	 * @param userService  The {@link UserService} for reading users
	 */
	@Autowired
	public SequenceFileRemoteRepositoryImpl(RemoteAPITokenService tokenService, UserService userService) {
		super(tokenService, userService, listTypeReference, objectTypeReference);
		this.tokenService = tokenService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Path downloadRemoteSequenceFile(String uri, RemoteAPI remoteAPI, MediaType... mediaTypes) {
		SequenceFile file = read(uri, remoteAPI);

		OAuthTokenRestTemplate restTemplate = new OAuthTokenRestTemplate(tokenService, remoteAPI);

		// add the sequence file message markdownConverter
		List<HttpMessageConverter<?>> converters = restTemplate.getMessageConverters();
		converters.add(new SequenceFileMessageConverter(file.getFileName()));
		restTemplate.setMessageConverters(converters);

		// add the application/fastq accept header
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setAccept(Arrays.asList(mediaTypes));
		HttpEntity<Path> requestEntity = new HttpEntity<>(requestHeaders);

		// get the file
		ResponseEntity<Path> exchange = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, Path.class);
		return exchange.getBody();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Path downloadRemoteSequenceFile(String uri, RemoteAPI remoteAPI) {
		return downloadRemoteSequenceFile(uri, remoteAPI, DEFAULT_DOWNLOAD_MEDIA_TYPE);
	}

}
