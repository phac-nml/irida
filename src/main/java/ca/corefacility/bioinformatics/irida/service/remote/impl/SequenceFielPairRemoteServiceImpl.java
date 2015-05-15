package ca.corefacility.bioinformatics.irida.service.remote.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.repositories.RemoteAPIRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.SequenceFilePairRemoteRepository;
import ca.corefacility.bioinformatics.irida.service.remote.SequenceFilePairRemoteService;
import ca.corefacility.bioinformatics.irida.web.controller.api.samples.RESTSampleSequenceFilesController;

@Service
public class SequenceFielPairRemoteServiceImpl extends RemoteServiceImpl<SequenceFilePair> implements
		SequenceFilePairRemoteService {

	public static final String SAMPLE_SEQENCE_FILE_PAIRS_REL = RESTSampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILE_PAIRS;

	private SequenceFilePairRemoteRepository repository;

	@Autowired
	public SequenceFielPairRemoteServiceImpl(SequenceFilePairRemoteRepository repository,
			RemoteAPIRepository remoteAPIRepository) {
		super(repository, remoteAPIRepository);
		this.repository = repository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<SequenceFilePair> getSequenceFilePairsForSample(Sample sample) {
		Link link = sample.getLink(SAMPLE_SEQENCE_FILE_PAIRS_REL);
		String href = link.getHref();

		RemoteAPI remoteApiForURI = getRemoteApiForURI(href);
		return repository.list(href, remoteApiForURI);
	}
}
