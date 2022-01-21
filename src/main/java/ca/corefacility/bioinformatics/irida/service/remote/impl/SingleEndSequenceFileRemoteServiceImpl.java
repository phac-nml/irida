package ca.corefacility.bioinformatics.irida.service.remote.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.repositories.RemoteAPIRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.SequenceFileRemoteRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.SingleEndSequenceFileRemoteRepository;
import ca.corefacility.bioinformatics.irida.service.remote.SingleEndSequenceFileRemoteService;
import ca.corefacility.bioinformatics.irida.web.controller.api.samples.RESTSampleSequenceFilesController;

/**
 * Implementation of {@link SingleEndSequenceFileRemoteService} using a
 * {@link SingleEndSequenceFileRemoteRepository}
 */
@Service
public class SingleEndSequenceFileRemoteServiceImpl extends SequencingObjectRemoteServiceImpl<SingleEndSequenceFile> implements
		SingleEndSequenceFileRemoteService {

	public static final String SAMPLE_SEQENCE_FILE_UNPAIRED_REL = RESTSampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILE_UNPAIRED;

	private SingleEndSequenceFileRemoteRepository repository;

	@Autowired
	public SingleEndSequenceFileRemoteServiceImpl(SingleEndSequenceFileRemoteRepository repository, SequenceFileRemoteRepository fileRemoteRepository,
			RemoteAPIRepository remoteAPIRepository) {
		super(repository, fileRemoteRepository, remoteAPIRepository);
		this.repository = repository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<SingleEndSequenceFile> getUnpairedFilesForSample(Sample sample) {
		Link link = sample.getLink(SAMPLE_SEQENCE_FILE_UNPAIRED_REL).map(i -> i).orElse(null);
		String href = link.getHref();

		RemoteAPI remoteApiForURI = getRemoteApiForURI(href);
		return repository.list(href, remoteApiForURI);
	}
}
