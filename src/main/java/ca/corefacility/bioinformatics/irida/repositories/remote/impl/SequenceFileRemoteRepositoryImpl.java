package ca.corefacility.bioinformatics.irida.repositories.remote.impl;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.core.token.TokenService;
import org.springframework.stereotype.Repository;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteSequenceFile;
import ca.corefacility.bioinformatics.irida.model.remote.resource.ListResourceWrapper;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RemoteResource;
import ca.corefacility.bioinformatics.irida.model.remote.resource.ResourceWrapper;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.repositories.remote.SequenceFileRemoteRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.resttemplate.OAuthTokenRestTemplate;
import ca.corefacility.bioinformatics.irida.repositories.remote.resttemplate.SequenceFileMessageConverter;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;

/**
 * Implementation of {@link SequenceFileRemoteRepository} using
 * {@link OAuthTokenRestTemplate} for making requests
 * 
 *
 */
@Repository
public class SequenceFileRemoteRepositoryImpl extends RemoteRepositoryImpl<SequenceFile> implements
		SequenceFileRemoteRepository {
	private static final ParameterizedTypeReference<ListResourceWrapper<SequenceFile>> listTypeReference = new ParameterizedTypeReference<ListResourceWrapper<SequenceFile>>() {
	};

	private static final ParameterizedTypeReference<ResourceWrapper<SequenceFile>> objectTypeReference = new ParameterizedTypeReference<ResourceWrapper<SequenceFile>>() {
	};

	// OAuth2 token storage service for making requests
	private final RemoteAPITokenService tokenService;
	// temporary directory for storing downloaded files
	private final Path tempDirectory;

	public static final MediaType DEFAULT_DOWNLOAD_MEDIA_TYPE = new MediaType("application", "fastq");

	/**
	 * Create a new SequenceFileRemoteRepositoryImpl
	 * 
	 * @param tokenService
	 *            The {@link TokenService} storing OAuth2 tokens
	 * @param tempDirectory
	 *            The temporary directory to store downloaded files
	 */
	@Autowired
	public SequenceFileRemoteRepositoryImpl(RemoteAPITokenService tokenService,
			@Qualifier("remoteFilesTempDirectory") Path tempDirectory) {
		super(tokenService, listTypeReference, objectTypeReference);
		this.tempDirectory = tempDirectory;
		this.tokenService = tokenService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Path downloadRemoteSequenceFile(RemoteSequenceFile sequenceFile, RemoteAPI remoteAPI,
			MediaType... mediaTypes) {
		OAuthTokenRestTemplate restTemplate = new OAuthTokenRestTemplate(tokenService, remoteAPI);

		// get the resource's URI
		String uri = sequenceFile.getHrefForRel(RemoteResource.SELF_REL);

		// add the sequence file message converter
		List<HttpMessageConverter<?>> converters = restTemplate.getMessageConverters();
		converters.add(new SequenceFileMessageConverter(tempDirectory));
		restTemplate.setMessageConverters(converters);

		// add the application/fastq accept header
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setAccept(Arrays.asList(mediaTypes));
		HttpEntity<Path> requestEntity = new HttpEntity<Path>(requestHeaders);

		// get the file
		ResponseEntity<Path> exchange = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, Path.class);
		return exchange.getBody();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Path downloadRemoteSequenceFile(RemoteSequenceFile sequenceFile, RemoteAPI remoteAPI) {
		return downloadRemoteSequenceFile(sequenceFile, remoteAPI, DEFAULT_DOWNLOAD_MEDIA_TYPE);
	}

}
