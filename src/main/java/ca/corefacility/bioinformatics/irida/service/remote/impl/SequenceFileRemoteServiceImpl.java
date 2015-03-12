package ca.corefacility.bioinformatics.irida.service.remote.impl;

import java.nio.file.Path;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.remote.RemoteSample;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteSequenceFile;
import ca.corefacility.bioinformatics.irida.repositories.remote.SequenceFileRemoteRepository;
import ca.corefacility.bioinformatics.irida.service.remote.SequenceFileRemoteService;

/**
 * Implementation of {@link SequenceFileRemoteService} using a
 * {@link SequenceFileRemoteRepository}
 * 
 *
 */
@Service
public class SequenceFileRemoteServiceImpl extends RemoteServiceImpl<RemoteSequenceFile> implements
		SequenceFileRemoteService {
	public static final String SAMPLE_SEQUENCE_FILES_REL = "sample/sequenceFiles";
	private final SequenceFileRemoteRepository repository;

	@Autowired
	public SequenceFileRemoteServiceImpl(SequenceFileRemoteRepository repository) {
		super(repository);
		this.repository = repository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<RemoteSequenceFile> getSequenceFilesForSample(RemoteSample sample) {
		String sequenceFilesRel = sample.getHrefForRel(SAMPLE_SEQUENCE_FILES_REL);
		return list(sequenceFilesRel, sample.getRemoteAPI());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Path downloadSequenceFile(RemoteSequenceFile sequenceFile) {
		return repository.downloadRemoteSequenceFile(sequenceFile, sequenceFile.getRemoteAPI());
	}
}
