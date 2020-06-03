package ca.corefacility.bioinformatics.irida.repositories.remote.impl;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;
import ca.corefacility.bioinformatics.irida.model.assembly.UploadedAssembly;
import ca.corefacility.bioinformatics.irida.model.remote.resource.ListResourceWrapper;
import ca.corefacility.bioinformatics.irida.model.remote.resource.ResourceWrapper;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.repositories.remote.GenomeAssemblyRemoteRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.resttemplate.OAuthTokenRestTemplate;
import ca.corefacility.bioinformatics.irida.repositories.remote.resttemplate.SequenceFileMessageConverter;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class GenomeAssemblyRemoteRepositoryImpl extends RemoteRepositoryImpl<UploadedAssembly> implements
        GenomeAssemblyRemoteRepository {

    public static final MediaType DEFAULT_DOWNLOAD_MEDIA_TYPE = new MediaType("application", "fasta");
    private static final ParameterizedTypeReference<ListResourceWrapper<UploadedAssembly>> listTypeReference = new ParameterizedTypeReference<ListResourceWrapper<UploadedAssembly>>() {
    };
    private static final ParameterizedTypeReference<ResourceWrapper<UploadedAssembly>> objectTypeReference = new ParameterizedTypeReference<ResourceWrapper<UploadedAssembly>>() {
    };

    RemoteAPITokenService tokenService;

    @Autowired
    public GenomeAssemblyRemoteRepositoryImpl(RemoteAPITokenService tokenService) {
        super(tokenService, listTypeReference, objectTypeReference);
        this.tokenService = tokenService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Path downloadRemoteSequenceFile(String uri, RemoteAPI remoteAPI, MediaType... mediaTypes) {
        GenomeAssembly file = read(uri, remoteAPI);

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
