package ca.corefacility.bioinformatics.irida.service.remote.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFileSnapshot;
import ca.corefacility.bioinformatics.irida.repositories.RemoteAPIRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.SampleRemoteRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.SequenceFileRemoteRepository;
import ca.corefacility.bioinformatics.irida.service.remote.SampleRemoteService;

import com.google.common.base.Strings;

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

	private final SequenceFileRemoteRepository fileRepository;

	@Autowired
	public SampleRemoteServiceImpl(SampleRemoteRepository sampleRemoteRepository,
			SequenceFileRemoteRepository fileRepository, RemoteAPIRepository apiRepository) {
		super(sampleRemoteRepository, apiRepository);
		this.fileRepository = fileRepository;
	}

	@Override
	public List<Sample> getSamplesForProject(Project project) {
		Link link = project.getLink(PROJECT_SAMPLES_REL);
		String samplesHref = link.getHref();
		return list(samplesHref, project.getRemoteAPI());
	}

	@Override
	public Page<Sample> searchSamplesForProject(Project project, String search, int page, int size) {
		List<Sample> samplesForProject = getSamplesForProject(project);
		if (!Strings.isNullOrEmpty(search)) {
			samplesForProject = samplesForProject.stream()
					.filter(s -> s.getSampleName().toLowerCase().contains(search.toLowerCase()))
					.collect(Collectors.toList());
		}

		int from = Math.max(0, page * size);
		int to = Math.min(samplesForProject.size(), (page + 1) * size);

		List<Sample> paged = samplesForProject.subList(from, to);
		return new PageImpl<>(paged, new PageRequest(page, size), samplesForProject.size());
	}

	@Override
	public Sample getSampleForSequenceFileSnapshot(SequenceFileSnapshot file) {
		String fileURI = file.getRemoteURI();
		RemoteAPI remoteApiForURI = getRemoteApiForURI(fileURI);
		SequenceFile read = fileRepository.read(fileURI, remoteApiForURI);

		Link sampleLink = read.getLink(FILE_SAMPLE_REL);

		return read(sampleLink.getHref());
	}

}
