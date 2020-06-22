package ca.corefacility.bioinformatics.irida.service.remote.impl;

import java.nio.file.Path;
import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.assembly.UploadedAssembly;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.repositories.RemoteAPIRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.GenomeAssemblyRemoteRepository;
import ca.corefacility.bioinformatics.irida.service.remote.GenomeAssemblyRemoteService;
import ca.corefacility.bioinformatics.irida.web.controller.api.samples.RESTSampleAssemblyController;

/**
 * An implementation of {@link GenomeAssemblyRemoteService} using a {@link GenomeAssemblyRemoteRepository} to read remote {@link ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly}
 */
@Service
public class GenomeAssemblyRemoteServiceImpl extends RemoteServiceImpl<UploadedAssembly>
		implements GenomeAssemblyRemoteService {

	public static final String SAMPLE_ASSEMBLY_REL = RESTSampleAssemblyController.REL_SAMPLE_ASSEMBLIES;

	private GenomeAssemblyRemoteRepository repository;

	/**
	 * Create a new remote service that interacts with the given repository
	 *
	 * @param repository          The {@link GenomeAssemblyRemoteRepository} to interact with remote apis
	 * @param remoteAPIRepository repository for storing and retrieving {@link RemoteAPI}s
	 */
	public GenomeAssemblyRemoteServiceImpl(GenomeAssemblyRemoteRepository repository,
			RemoteAPIRepository remoteAPIRepository) {
		super(repository, remoteAPIRepository);

		this.repository = repository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UploadedAssembly> getGenomeAssembliesForSample(Sample sample) {
		Link link = sample.getLink(SAMPLE_ASSEMBLY_REL);
		String href = link.getHref();

		RemoteAPI remoteApiForURI = getRemoteApiForURI(href);
		return repository.list(href, remoteApiForURI);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UploadedAssembly mirrorAssembly(UploadedAssembly seqObject) {
		String fileHref = seqObject.getSelfHref();
		RemoteAPI api = getRemoteApiForURI(fileHref);
		Path downloadRemoteSequenceFile = repository.downloadRemoteAssembly(fileHref, api);
		seqObject.setFile(downloadRemoteSequenceFile);

		return seqObject;
	}
}
