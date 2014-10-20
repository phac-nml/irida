package ca.corefacility.bioinformatics.irida.service.remote.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteProject;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteSample;
import ca.corefacility.bioinformatics.irida.repositories.remote.ProjectRemoteRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.SampleRemoteRepository;
import ca.corefacility.bioinformatics.irida.service.remote.SampleRemoteService;

@Service
public class SampleRemoteServiceImpl extends RemoteServiceImpl<RemoteSample> implements SampleRemoteService {
	public static final String PROJECT_SAMPLES_REL = "project/samples";

	@Autowired
	public SampleRemoteServiceImpl(SampleRemoteRepository sampleRemoteRepository,
			ProjectRemoteRepository projectRemoteRepository) {
		super(sampleRemoteRepository);
	}

	@Override
	public List<RemoteSample> getSamplesForProject(RemoteProject project, RemoteAPI api) {
		String samplesHref = project.getHrefForRel(PROJECT_SAMPLES_REL);

		return list(samplesHref, api);
	}

}
