package ca.corefacility.bioinformatics.irida.service.remote.impl;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.repositories.RemoteAPIRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.SequenceFilePairRemoteRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.SequenceFileRemoteRepository;
import ca.corefacility.bioinformatics.irida.service.remote.SequenceFilePairRemoteService;
import ca.corefacility.bioinformatics.irida.web.controller.api.samples.RESTSampleSequenceFilesController;

@Service
public class SequenceFielPairRemoteServiceImpl extends RemoteServiceImpl<SequenceFilePair>
		implements SequenceFilePairRemoteService {

	public static final String SAMPLE_SEQENCE_FILE_PAIRS_REL = RESTSampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILE_PAIRS;

	private SequenceFilePairRemoteRepository repository;
	private SequenceFileRemoteRepository sequenceFileRemoteRepository;

	@Autowired
	public SequenceFielPairRemoteServiceImpl(SequenceFilePairRemoteRepository repository,
			SequenceFileRemoteRepository sequenceFileRemoteRepository, RemoteAPIRepository remoteAPIRepository) {
		super(repository, remoteAPIRepository);
		this.repository = repository;
		this.sequenceFileRemoteRepository = sequenceFileRemoteRepository;
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

	@Override
	public SequenceFilePair mirrorPair(SequenceFilePair pair) {

		Set<SequenceFile> files = pair.getFiles();

		for (SequenceFile file : files) {
			String fileHref = file.getSelfHref();
			RemoteAPI api = getRemoteApiForURI(fileHref);
			Path downloadRemoteSequenceFile = sequenceFileRemoteRepository.downloadRemoteSequenceFile(fileHref, api);
			file.setFile(downloadRemoteSequenceFile);
		}

		return pair;
	}
}
