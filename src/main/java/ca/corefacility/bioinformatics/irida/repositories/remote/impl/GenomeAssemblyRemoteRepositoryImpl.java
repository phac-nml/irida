package ca.corefacility.bioinformatics.irida.repositories.remote.impl;

import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;
import ca.corefacility.bioinformatics.irida.model.remote.resource.ListResourceWrapper;
import ca.corefacility.bioinformatics.irida.model.remote.resource.ResourceWrapper;
import ca.corefacility.bioinformatics.irida.repositories.remote.GenomeAssemblyRemoteRepository;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;

public class GenomeAssemblyRemoteRepositoryImpl extends RemoteRepositoryImpl<GenomeAssembly> implements
        GenomeAssemblyRemoteRepository {

    private static final ParameterizedTypeReference<ListResourceWrapper<GenomeAssembly>> listTypeReference = new ParameterizedTypeReference<ListResourceWrapper<GenomeAssembly>>() {
    };
    private static final ParameterizedTypeReference<ResourceWrapper<GenomeAssembly>> objectTypeReference = new ParameterizedTypeReference<ResourceWrapper<GenomeAssembly>>() {
    };

    @Autowired
    public GenomeAssemblyRemoteRepositoryImpl(RemoteAPITokenService tokenService) {
        super(tokenService, listTypeReference, objectTypeReference);
    }
}
