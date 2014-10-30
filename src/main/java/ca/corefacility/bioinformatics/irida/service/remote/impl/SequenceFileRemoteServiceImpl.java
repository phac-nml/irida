package ca.corefacility.bioinformatics.irida.service.remote.impl;

import java.nio.file.Path;
import java.util.List;

import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteSample;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteSequenceFile;
import ca.corefacility.bioinformatics.irida.repositories.remote.SequenceFileRemoteRepository;
import ca.corefacility.bioinformatics.irida.service.remote.SequenceFilesRemoteService;

/**
 * Implementation of {@link SequenceFilesRemoteService} using a
 * {@link SequenceFileRemoteRepository}
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
@Service
public class SequenceFilesRemoteServiceImpl extends RemoteServiceImpl<RemoteSequenceFile> implements
		SequenceFilesRemoteService {
	public static final String SAMPLE_SEQUENCE_FILES_REL = "sample/sequenceFiles";
	private final SequenceFileRemoteRepository repository;

	public SequenceFilesRemoteServiceImpl(SequenceFileRemoteRepository repository) {
		super(repository);
		this.repository = repository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<RemoteSequenceFile> getSequenceFilesForSample(RemoteSample sample, RemoteAPI api) {
		String sequenceFilesRel = sample.getHrefForRel(SAMPLE_SEQUENCE_FILES_REL);
		return list(sequenceFilesRel, api);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Path downloadSequenceFile(RemoteSequenceFile sequenceFile, RemoteAPI api) {
		return repository.downloadRemoteSequenceFile(sequenceFile, api);
	}
}
