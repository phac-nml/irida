package ca.corefacility.bioinformatics.irida.service.remote.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.repositories.RemoteAPIRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.SampleRemoteRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.SequenceFilePairRemoteRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.SingleEndSequenceFileRemoteRepository;
import ca.corefacility.bioinformatics.irida.service.remote.SampleRemoteService;

/**
 * Implementation of {@link SampleRemoteService} using
 * {@link SampleRemoteRepository}
 * 
 *
 */
@Service
public class SampleRemoteServiceImpl extends RemoteServiceImpl<Sample> implements SampleRemoteService {
	public static final String PROJECT_SAMPLES_REL = "project/samples";
	public static final String SAMPLES_CACHE_NAME = "samplesForProject";

	public static final String FILE_SAMPLE_REL = "sample";


	@Autowired
	public SampleRemoteServiceImpl(SampleRemoteRepository sampleRemoteRepository,
			SequenceFilePairRemoteRepository pairRemoteRepository,
			SingleEndSequenceFileRemoteRepository unpairedRemoteRepository, RemoteAPIRepository apiRepository) {
		super(sampleRemoteRepository, apiRepository);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Sample> getSamplesForProject(Project project) {
		Link link = project.getLink(PROJECT_SAMPLES_REL);
		String samplesHref = link.getHref();
		return list(samplesHref, project.getRemoteStatus().getApi());
	}
}
