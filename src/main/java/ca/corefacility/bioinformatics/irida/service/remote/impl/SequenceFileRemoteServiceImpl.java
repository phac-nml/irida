package ca.corefacility.bioinformatics.irida.service.remote.impl;

import java.nio.file.Path;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.repositories.RemoteAPIRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.SequenceFileRemoteRepository;
import ca.corefacility.bioinformatics.irida.service.remote.SequenceFileRemoteService;

/**
 * Implementation of {@link SequenceFileRemoteService} using a
 * {@link SequenceFileRemoteRepository}
 * 
 *
 */
@Service
public class SequenceFileRemoteServiceImpl extends RemoteServiceImpl<SequenceFile> implements SequenceFileRemoteService {
	public static final String SAMPLE_SEQUENCE_FILES_REL = "sample/sequenceFiles";
	private final SequenceFileRemoteRepository repository;

	@Autowired
	public SequenceFileRemoteServiceImpl(SequenceFileRemoteRepository repository, RemoteAPIRepository apiRepository) {
		super(repository, apiRepository);
		this.repository = repository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<SequenceFile> getSequenceFilesForSample(Sample sample) {
		Link link = sample.getLink(SAMPLE_SEQUENCE_FILES_REL);
		String sequenceFilesRel = link.getHref();
		return list(sequenceFilesRel, sample.getRemoteAPI());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Path downloadSequenceFile(SequenceFile sequenceFile) {
		return repository.downloadRemoteSequenceFile(sequenceFile, sequenceFile.getRemoteAPI());
	}
}
