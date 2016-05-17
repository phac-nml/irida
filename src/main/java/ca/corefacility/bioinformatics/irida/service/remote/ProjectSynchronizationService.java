package ca.corefacility.bioinformatics.irida.service.remote;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.exceptions.IridaOAuthException;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteStatus.SyncStatus;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

@Service
public class ProjectSynchronizationService {
	private ProjectService projectService;
	private SampleService sampleService;
	private SequencingObjectService objectService;

	private ProjectRemoteService projectRemoteService;
	private SampleRemoteService sampleRemoteService;
	private SingleEndSequenceFileRemoteService singleEndRemoteService;
	private SequenceFilePairRemoteService pairRemoteService;

	@Autowired
	public ProjectSynchronizationService(ProjectService projectService, SampleService sampleService,
			SequencingObjectService objectService, ProjectRemoteService projectRemoteService,
			SampleRemoteService sampleRemoteService, SingleEndSequenceFileRemoteService singleEndRemoteService,
			SequenceFilePairRemoteService pairRemoteService) {

		this.projectService = projectService;
		this.sampleService = sampleService;
		this.objectService = objectService;
		this.projectRemoteService = projectRemoteService;
		this.sampleRemoteService = sampleRemoteService;
		this.singleEndRemoteService = singleEndRemoteService;
		this.pairRemoteService = pairRemoteService;
	}

	public void findMarkedProjectsToSync() {
		List<Project> markedProjects = projectService.getProjectsWithRemoteSyncStatus(SyncStatus.MARKED);

		for (Project project : markedProjects) {
			// Set the correct authorization for the user who's syncing the
			// project

			try {
				syncProject(project);
			} catch (IridaOAuthException e) {
				project.getRemoteStatus().setSyncStatus(SyncStatus.UNAUTHORIZED);
			}
		}

	}

	public void syncProject(Project project) {
		String projectURL = project.getRemoteStatus().getURL();

		Project readProject = projectRemoteService.read(projectURL);

		List<Sample> readSamplesForProject = sampleRemoteService.getSamplesForProject(readProject);

		for (Sample s : readSamplesForProject) {
			s.setId(null);
			syncSample(s, project);
		}

	}

	public void syncSample(Sample sample, Project project) {
		sampleService.create(sample);

		projectService.addSampleToProject(project, sample);

		List<SequenceFilePair> sequenceFilePairsForSample = pairRemoteService.getSequenceFilePairsForSample(sample);

		for (SequenceFilePair pair : sequenceFilePairsForSample) {
			pair.setId(null);
			syncSequenceFilePair(pair, sample);
		}

		List<SingleEndSequenceFile> unpairedFilesForSample = singleEndRemoteService.getUnpairedFilesForSample(sample);

		for (SingleEndSequenceFile file : unpairedFilesForSample) {
			file.setId(null);
			syncSingleEndSequenceFile(file);
		}
	}

	public void syncSingleEndSequenceFile(SingleEndSequenceFile file) {
		// objectService.create(file);
	}

	public void syncSequenceFilePair(SequenceFilePair pair, Sample sample) {
		pair = pairRemoteService.mirrorPair(pair);

		pair.getFiles().forEach(s -> s.setId(null));

		objectService.createSequencingObjectInSample(pair, sample);
	}
}
