package ca.corefacility.bioinformatics.irida.service.remote;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.exceptions.IridaOAuthException;
import ca.corefacility.bioinformatics.irida.model.MutableIridaThing;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteStatus;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteStatus.SyncStatus;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteSynchronizable;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.security.ProjectSynchronizationAuthenticationToken;
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
			/*
			 * Set the correct authorization for the user who's syncing the
			 * project
			 */
			User readBy = project.getRemoteStatus().getReadBy();
			setAuthentication(readBy);

			logger.debug("Syncing project at " + project.getRemoteStatus().getURL());

			try {
				syncProject(project);
			} catch (IridaOAuthException e) {
				logger.debug(
						"Can't sync project project " + project.getRemoteStatus().getURL() + " due to oauth error:", e);
				project.getRemoteStatus().setSyncStatus(SyncStatus.UNAUTHORIZED);
				projectService.update(project);
			} finally {
				// clear the context holder when you're done
				SecurityContextHolder.clearContext();

				logger.debug("Done project " + project.getRemoteStatus().getURL());
			}

		}

	}

	/**
	 * Synchronize a given {@link Project} to the local installation.
	 * 
	 * @param project
	 *            the {@link Project} to synchronize. This should have been read
	 *            from a remote api.
	 */
	private void syncProject(Project project) {
		project.getRemoteStatus().setSyncStatus(SyncStatus.UPDATING);
		projectService.update(project);

		String projectURL = project.getRemoteStatus().getURL();

		Project readProject = projectRemoteService.read(projectURL);

		// if project was updated remotely, update it here
		if (checkForChanges(project.getRemoteStatus(), readProject)) {
			logger.debug("found changes for project " + readProject.getSelfHref());

			// ensure we use the same IDs
			readProject = updateIds(project, readProject);

			project = projectService.update(readProject);
		}

		List<Join<Project, Sample>> localSamples = sampleService.getSamplesForProject(project);

		// get all the samples by their url
		Map<String, Sample> samplesByUrl = new HashMap<>();
		localSamples.forEach(j -> {
			Sample sample = j.getObject();
			String url = sample.getRemoteStatus().getURL();

			samplesByUrl.put(url, sample);
		});

		List<Sample> readSamplesForProject = sampleRemoteService.getSamplesForProject(readProject);

		for (Sample s : readSamplesForProject) {
			s.setId(null);
			syncSample(s, project, samplesByUrl);
		}

		project.getRemoteStatus().setSyncStatus(SyncStatus.SYNCHRONIZED);
		projectService.update(project);
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
	public void syncSample(Sample sample, Project project, Map<String, Sample> existingSamples) {
		Sample localSample;
		if (existingSamples.containsKey(sample.getRemoteStatus().getURL())) {
			// if the sample already exists check if it's been updated
			localSample = existingSamples.get(sample.getRemoteStatus().getURL());

			// if there's changes, update the sample
			if (checkForChanges(localSample.getRemoteStatus(), sample)) {
				logger.debug("found changes for sample " + sample.getSelfHref());

				// ensure the ids are properly set
				sample = updateIds(localSample, sample);
				sample.getRemoteStatus().setSyncStatus(SyncStatus.UPDATING);

				localSample = sampleService.update(sample);
			}

		} else {
			// if the sample doesn't already exist create it
			sample.getRemoteStatus().setSyncStatus(SyncStatus.UPDATING);
			localSample = sampleService.create(sample);
			projectService.addSampleToProject(project, sample);
		}

		// get the local files and organize by their url
		Collection<SampleSequencingObjectJoin> localPairs = objectService.getSequencesForSampleOfType(localSample,
				SequenceFilePair.class);
		Map<String, SequenceFilePair> pairsByUrl = new HashMap<>();
		localPairs.forEach(j -> {
			SequenceFilePair pair = (SequenceFilePair) j.getObject();
			String url = pair.getRemoteStatus().getURL();

			pairsByUrl.put(url, pair);
		});

		List<SequenceFilePair> sequenceFilePairsForSample = pairRemoteService.getSequenceFilePairsForSample(sample);

		for (SequenceFilePair pair : sequenceFilePairsForSample) {
			if (!pairsByUrl.containsKey(pair.getRemoteStatus().getURL())) {
				pair.setId(null);
				syncSequenceFilePair(pair, localSample);
			}
		}

		List<SingleEndSequenceFile> unpairedFilesForSample = singleEndRemoteService.getUnpairedFilesForSample(sample);

		for (SingleEndSequenceFile file : unpairedFilesForSample) {
			file.setId(null);
			syncSingleEndSequenceFile(file);
		}

		localSample.getRemoteStatus().setSyncStatus(SyncStatus.SYNCHRONIZED);
		sampleService.update(localSample);
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
		pair = pairRemoteService.mirrorSequencingObject(pair);

		pair.getFiles().forEach(s -> {
			s.setId(null);
			s.getRemoteStatus().setSyncStatus(SyncStatus.SYNCHRONIZED);
		});

		objectService.createSequencingObjectInSample(pair, sample);

		RemoteStatus pairStatus = pair.getRemoteStatus();
		pairStatus.setSyncStatus(SyncStatus.SYNCHRONIZED);

		objectService.updateRemoteStatus(pair.getId(), pairStatus);
	}

	/**
	 * Check if an object has been updated since it was last read
	 * 
	 * @param originalStatus
	 *            the original object's {@link RemoteStatus}
	 * @param read
	 *            the newly read {@link RemoteSynchronizable} object
	 * @return true if the object has changed, false if not
	 */
	private boolean checkForChanges(RemoteStatus originalStatus, RemoteSynchronizable read) {
		return originalStatus.getRemoteHashCode() != read.hashCode();
	}

	/**
	 * Update the IDs of a newly read object and it's associated RemoteStatus to
	 * the IDs of a local copy
	 * 
	 * @param original
	 *            the original object
	 * @param updated
	 *            the newly read updated object
	 * @return the enhanced newly read object
	 */
	private <Type extends MutableIridaThing & RemoteSynchronizable> Type updateIds(Type original, Type updated) {
		updated.setId(original.getId());
		updated.getRemoteStatus().setId(original.getRemoteStatus().getId());

		return updated;
	}

	/**
	 * Set the given user's authentication in the SecurityContextHolder
	 * 
	 * @param userAuthentication
	 *            The {@link User} to set in the context holder
	 */
	private void setAuthentication(User user) {
		ProjectSynchronizationAuthenticationToken userAuthentication = new ProjectSynchronizationAuthenticationToken(
				user);

		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(userAuthentication);
		SecurityContextHolder.setContext(context);
	}
}
