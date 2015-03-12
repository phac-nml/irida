package ca.corefacility.bioinformatics.irida.service.remote.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.remote.RemoteProject;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteSample;
import ca.corefacility.bioinformatics.irida.repositories.remote.SampleRemoteRepository;
import ca.corefacility.bioinformatics.irida.service.remote.SampleRemoteService;

import com.google.common.base.Strings;

/**
 * Implementation of {@link SampleRemoteService} using
 * {@link SampleRemoteRepository}
 * 
 *
 */
@Service
public class SampleRemoteServiceImpl extends RemoteServiceImpl<RemoteSample> implements SampleRemoteService {
	public static final String PROJECT_SAMPLES_REL = "project/samples";
	public static final String SAMPLES_CACHE_NAME = "samplesForProject";

	@Autowired
	public SampleRemoteServiceImpl(SampleRemoteRepository sampleRemoteRepository) {
		super(sampleRemoteRepository);
	}

	@Override
	public List<RemoteSample> getSamplesForProject(RemoteProject project) {
		String samplesHref = project.getHrefForRel(PROJECT_SAMPLES_REL);
		return list(samplesHref, project.getRemoteAPI());
	}

	@Override
	public Page<RemoteSample> searchSamplesForProject(RemoteProject project, String search, int page, int size) {
		List<RemoteSample> samplesForProject = getSamplesForProject(project);
		if (!Strings.isNullOrEmpty(search)) {
			samplesForProject = samplesForProject.stream()
					.filter(s -> s.getSampleName().toLowerCase().contains(search.toLowerCase()))
					.collect(Collectors.toList());
		}

		int from = Math.max(0, page * size);
		int to = Math.min(samplesForProject.size(), (page + 1) * size);

		List<RemoteSample> paged = samplesForProject.subList(from, to);
		return new PageImpl<>(paged, new PageRequest(page, size), samplesForProject.size());
	}

}
