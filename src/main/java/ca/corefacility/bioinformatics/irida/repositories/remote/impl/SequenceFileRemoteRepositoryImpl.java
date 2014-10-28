package ca.corefacility.bioinformatics.irida.repositories.remote.impl;

import java.nio.file.Path;
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
import ca.corefacility.bioinformatics.irida.repositories.remote.SequenceFileRemoteRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.resttemplate.OAuthTokenRestTemplate;
import ca.corefacility.bioinformatics.irida.repositories.remote.resttemplate.SequenceFileMessageConverter;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;

import com.google.common.collect.Lists;

/**
 * Implementation of {@link SequenceFileRemoteRepository}
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
@Repository
public class SequenceFileRemoteRepositoryImpl extends RemoteRepositoryImpl<RemoteSequenceFile> implements
		SequenceFileRemoteRepository {
	private static final ParameterizedTypeReference<ListResourceWrapper<RemoteSequenceFile>> listTypeReference = new ParameterizedTypeReference<ListResourceWrapper<RemoteSequenceFile>>() {
	};

	private static final ParameterizedTypeReference<ResourceWrapper<RemoteSequenceFile>> objectTypeReference = new ParameterizedTypeReference<ResourceWrapper<RemoteSequenceFile>>() {
	};

	// OAuth2 token storage service for making requests
	private final RemoteAPITokenService tokenService;
	// temporary directory for storing downloaded files
	private final Path tempDirectory;

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
	public Path downloadRemoteSequenceFile(RemoteSequenceFile sequenceFile, RemoteAPI remoteAPI) {
		OAuthTokenRestTemplate restTemplate = new OAuthTokenRestTemplate(tokenService, remoteAPI);

		// get the resource's URI
		String uri = sequenceFile.getHrefForRel(RemoteResource.SELF_REL);

		// add the sequence file message converter
		List<HttpMessageConverter<?>> converters = restTemplate.getMessageConverters();
		converters.add(new SequenceFileMessageConverter(tempDirectory));
		restTemplate.setMessageConverters(converters);

		// add the application/fastq accept header
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setAccept(Lists.newArrayList(new MediaType("application", "fastq")));
		HttpEntity<Path> requestEntity = new HttpEntity<Path>(requestHeaders);

		// get the file
		ResponseEntity<Path> exchange = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, Path.class);
		return exchange.getBody();
	}

}
