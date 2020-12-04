package ca.corefacility.bioinformatics.irida.service.remote.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.Fast5Object;
import ca.corefacility.bioinformatics.irida.repositories.RemoteAPIRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.Fast5ObjectRemoteRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.SequenceFileRemoteRepository;
import ca.corefacility.bioinformatics.irida.service.remote.Fast5ObjectRemoteService;
import ca.corefacility.bioinformatics.irida.web.controller.api.samples.RESTSampleSequenceFilesController;

/**
 * An implementation of {@link Fast5ObjectRemoteService} using a {@link Fast5ObjectRemoteRepository} to read remote {@link Fast5Object}
 */
@Service
public class Fast5ObjectRemoteServiceImpl extends SequencingObjectRemoteServiceImpl<Fast5Object> implements Fast5ObjectRemoteService {

	public static final String REL_SAMPLE_SEQUENCE_FILE_FAST5 = RESTSampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILE_FAST5;

	private Fast5ObjectRemoteRepository repository;

	@Autowired
	public Fast5ObjectRemoteServiceImpl(Fast5ObjectRemoteRepository repository,
			SequenceFileRemoteRepository fileRemoteRepository,
			RemoteAPIRepository remoteAPIRepository) {
		super(repository, fileRemoteRepository, remoteAPIRepository);

		this.repository = repository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Fast5Object> getFast5FilesForSample(Sample sample) {
		Link link = sample.getLink(REL_SAMPLE_SEQUENCE_FILE_FAST5);
		String href = link.getHref();

		RemoteAPI remoteApiForURI = getRemoteApiForURI(href);
		return repository.list(href, remoteApiForURI);
	}
}
