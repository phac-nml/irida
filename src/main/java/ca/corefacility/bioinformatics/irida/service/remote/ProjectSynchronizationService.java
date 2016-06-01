package ca.corefacility.bioinformatics.irida.service.remote;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;

import ca.corefacility.bioinformatics.irida.exceptions.IridaOAuthException;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteStatus;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteStatus.SyncStatus;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

/**
 * Service class to run a project synchornization task. Ths class will be
 * responsible for communicating with Remote IRIDA installations and pulling
 * metadata and sequencing data into the local installation.
 */
@Service
public class ProjectSynchronizationService {
	private static final Logger logger = LoggerFactory.getLogger(ProjectSynchronizationService.class);

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

	/**
	 * Find projects which should be synchronized and launch a synchornization
	 * task.
	 */
	public void findMarkedProjectsToSync() {
		List<Project> markedProjects = projectService.getProjectsWithRemoteSyncStatus(SyncStatus.MARKED);

		logger.debug("Checking for projects to sync");

		for (Project project : markedProjects) {
			// Set the correct authorization for the user who's syncing the
			// project
			logger.debug("Syncing project at " + project.getRemoteStatus().getURL());
			project.getRemoteStatus().setSyncStatus(SyncStatus.UPDATING);
			projectService.update(project);
			try {
				syncProject(project);

				project.getRemoteStatus().setSyncStatus(SyncStatus.SYNCHRONIZED);

				projectService.update(project);
			} catch (IridaOAuthException e) {
				logger.debug(
						"Can't sync project project " + project.getRemoteStatus().getURL() + " due to oauth error:", e);
				project.getRemoteStatus().setSyncStatus(SyncStatus.UNAUTHORIZED);
			}

			logger.debug("Done project " + project.getRemoteStatus().getURL());
		}

	}

	/**
	 * Synchronize a given {@link Project} to the local installation.
	 * 
	 * @param project
	 *            the {@link Project} to synchronize. This should have been read
	 *            from a remote api.
	 */
	public void syncProject(Project project) {
		String projectURL = project.getRemoteStatus().getURL();

		Project readProject = projectRemoteService.read(projectURL);

		List<Sample> readSamplesForProject = sampleRemoteService.getSamplesForProject(readProject);

		for (Sample s : readSamplesForProject) {
			s.setId(null);
			syncSample(s, project);
		}

	}

	/**
	 * Synchronize a given {@link Sample} to the local installation.
	 * 
	 * @param sample
	 *            the {@link Sample} to synchronize. This should have been read
	 *            from a remote api.
	 * @param project
	 *            The {@link Project} the {@link Sample} belongs in.
	 */
	public void syncSample(Sample sample, Project project) {
		sample.getRemoteStatus().setSyncStatus(SyncStatus.UPDATING);
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

		sample.getRemoteStatus().setSyncStatus(SyncStatus.SYNCHRONIZED);
		sampleService.update(sample);
	}

	// TODO: Fill out this method
	public void syncSingleEndSequenceFile(SingleEndSequenceFile file) {
		// objectService.create(file);
	}

	/**
	 * Synchronize a given {@link SequenceFilePair} to the local installation.
	 * 
	 * @param pair
	 *            the {@link SequenceFilePair} to sync. This should have been
	 *            read from a remote api.
	 * @param sample
	 *            The {@link Sample} to add the pair to.
	 */
	public void syncSequenceFilePair(SequenceFilePair pair, Sample sample) {
		pair.getRemoteStatus().setSyncStatus(SyncStatus.UPDATING);
		pair = pairRemoteService.mirrorPair(pair);

		pair.getFiles().forEach(s -> {
			s.setId(null);
			s.getRemoteStatus().setSyncStatus(SyncStatus.SYNCHRONIZED);
		});

		objectService.createSequencingObjectInSample(pair, sample);

		RemoteStatus pairStatus = pair.getRemoteStatus();
		pairStatus.setSyncStatus(SyncStatus.SYNCHRONIZED);

		objectService.updateRemoteStatus(pair.getId(), pairStatus);
	}
}
