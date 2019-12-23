package ca.corefacility.bioinformatics.irida.service.remote;

import ca.corefacility.bioinformatics.irida.exceptions.IridaOAuthException;
import ca.corefacility.bioinformatics.irida.exceptions.ProjectSynchronizationException;
import ca.corefacility.bioinformatics.irida.model.MutableIridaThing;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ProjectSyncFrequency;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteStatus;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteStatus.SyncStatus;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteSynchronizable;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.security.ProjectSynchronizationAuthenticationToken;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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
	private MetadataTemplateService metadataTemplateService;

	private ProjectRemoteService projectRemoteService;
	private SampleRemoteService sampleRemoteService;
	private SingleEndSequenceFileRemoteService singleEndRemoteService;
	private SequenceFilePairRemoteService pairRemoteService;
	private RemoteAPITokenService tokenService;

	@Autowired
	public ProjectSynchronizationService(ProjectService projectService, SampleService sampleService,
			SequencingObjectService objectService, MetadataTemplateService metadataTemplateService, ProjectRemoteService projectRemoteService,
			SampleRemoteService sampleRemoteService, SingleEndSequenceFileRemoteService singleEndRemoteService,
			SequenceFilePairRemoteService pairRemoteService, RemoteAPITokenService tokenService) {

		this.projectService = projectService;
		this.sampleService = sampleService;
		this.objectService = objectService;
		this.metadataTemplateService = metadataTemplateService;
		this.projectRemoteService = projectRemoteService;
		this.sampleRemoteService = sampleRemoteService;
		this.singleEndRemoteService = singleEndRemoteService;
		this.pairRemoteService = pairRemoteService;
		this.tokenService = tokenService;
	}

	/**
	 * Method checking for remote projects that have passed their frequency
	 * time. It will mark them as {@link SyncStatus#MARKED}
	 */
	public void findProjectsToMark() {
		List<Project> remoteProjects = projectService.getRemoteProjects();

		for (Project p : remoteProjects) {
			// check the frequency for each remote project
			RemoteStatus remoteStatus = p.getRemoteStatus();
			Date lastUpdate = remoteStatus.getLastUpdate();
			ProjectSyncFrequency syncFrequency = p.getSyncFrequency();

			// if the project is set to be synched
			if (syncFrequency != null) {
				if (syncFrequency != ProjectSyncFrequency.NEVER) {
					/*
					 * find the next sync date and see if it's passed. if it has
					 * set as MARKED
					 */
					Date nextSync = DateUtils.addDays(lastUpdate, syncFrequency.getDays());

					if (nextSync.before(new Date())) {
						Map<String, Object> updates = new HashMap<>();
						remoteStatus.setSyncStatus(SyncStatus.MARKED);
						updates.put("remoteStatus", remoteStatus);
						projectService.updateProjectSettings(p, updates);
					}
				} else if (remoteStatus.getSyncStatus() != RemoteStatus.SyncStatus.UNSYNCHRONIZED) {
					/*
					 * if a sync frequency is NEVER and it's status isn't
					 * UNSYNCHRONIZED, it should be set as such
					 */
					remoteStatus.setSyncStatus(SyncStatus.UNSYNCHRONIZED);
					Map<String, Object> updates = new HashMap<>();
					updates.put("remoteStatus", remoteStatus);
					projectService.updateProjectSettings(p, updates);
				}
			}
		}
	}

	/**
	 * Find projects which should be synchronized and launch a synchornization
	 * task.
	 */
	public synchronized void findMarkedProjectsToSync() {
		// mark any projects which should be synched first
		findProjectsToMark();

		List<Project> markedProjects = projectService.getProjectsWithRemoteSyncStatus(SyncStatus.MARKED);

		logger.trace("Checking for projects to sync");

		for (Project project : markedProjects) {
			/*
			 * Set the correct authorization for the user who's syncing the
			 * project
			 */
			User readBy = project.getRemoteStatus().getReadBy();
			setAuthentication(readBy);

			logger.trace("Syncing project at " + project.getRemoteStatus().getURL());

			try {
				RemoteAPI api = project.getRemoteStatus().getApi();
				tokenService.updateTokenFromRefreshToken(api);

				syncProject(project);
			} catch (IridaOAuthException e) {
				logger.trace("Can't sync project " + project.getRemoteStatus().getURL() + " due to oauth error:", e);
				//re-reading project to get updated version
				project = projectService.read(project.getId());
				project.getRemoteStatus().setSyncStatus(SyncStatus.UNAUTHORIZED);
				projectService.update(project);
			} catch (Exception e) {
				logger.debug("An error occurred while synchronizing project " + project.getRemoteStatus().getURL(), e);
				//re-reading project to get updated version
				project = projectService.read(project.getId());
				project.getRemoteStatus().setSyncStatus(SyncStatus.ERROR);
				projectService.update(project);
			} finally {
				// clear the context holder when you're done
				SecurityContextHolder.clearContext();

				logger.trace("Done project " + project.getRemoteStatus().getURL());
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
		project.getRemoteStatus().setLastUpdate(new Date());
		projectService.update(project);

		String projectURL = project.getRemoteStatus().getURL();

		Project readProject = projectRemoteService.read(projectURL);

		// ensure we use the same IDs
		readProject = updateIds(project, readProject);

		// if project was updated remotely, update it here
		if (checkForChanges(project.getRemoteStatus(), readProject)) {
			logger.debug("found changes for project " + readProject.getSelfHref());

			// need to keep the status and frequency of the local project
			RemoteStatus originalStatus = project.getRemoteStatus();
			readProject.getRemoteStatus().setSyncStatus(originalStatus.getSyncStatus());
			readProject.setSyncFrequency(project.getSyncFrequency());

			project = projectService.update(readProject);
		}

		List<Join<Project, Sample>> localSamples = sampleService.getSamplesForProject(project);

		// get all the samples by their url
		Map<String, Sample> samplesByUrl = new HashMap<>();
		localSamples.forEach(j -> {
			Sample sample = j.getObject();
			
			// If a user has added a sample for some reason, ignore it
			if (sample.getRemoteStatus() != null) {
				String url = sample.getRemoteStatus().getURL();

				samplesByUrl.put(url, sample);
			} else {
				logger.warn("Sample " + sample.getId() + " is not a remote sample.  It will not be synchronized.");
			}
		});

		//read the remote samples from the remote API
		List<Sample> readSamplesForProject = sampleRemoteService.getSamplesForProject(readProject);

		//get a list of all remote URLs in the project
		Set<String> remoteUrls = readSamplesForProject.stream()
				.map(s -> s.getRemoteStatus()
						.getURL())
				.collect(Collectors.toSet());

		// Check for local samples which no longer exist by URL
		Set<String> localUrls = new HashSet<>(samplesByUrl.keySet());
		//remove any URL from the local list that we've seen remotely
		remoteUrls.forEach(s -> {
			localUrls.remove(s);
		});

		// if any URLs still exist in localUrls, it must have been deleted remotely
		for (String localUrl : localUrls) {
			logger.trace("Sample " + localUrl + " has been removed remotely.  Removing from local project.");

			projectService.removeSampleFromProject(project, samplesByUrl.get(localUrl));
			samplesByUrl.remove(localUrl);
		}

		List<ProjectSynchronizationException> syncExceptions = new ArrayList<>();
		for (Sample s : readSamplesForProject) {
			s.setId(null);
			s = syncSampleMetadata(s);
			List<ProjectSynchronizationException> syncExceptionsSample = syncSample(s, project, samplesByUrl);

			syncExceptions.addAll(syncExceptionsSample);
		}

		// re-read project to ensure any updates are reflected
		project = projectService.read(project.getId());
		project.setRemoteStatus(readProject.getRemoteStatus());

		if (syncExceptions.isEmpty()) {
			project.getRemoteStatus().setSyncStatus(SyncStatus.SYNCHRONIZED);
		} else {
			project.getRemoteStatus().setSyncStatus(SyncStatus.ERROR);

			logger.error("Error syncing project " + project.getId() + " setting sync status to ERROR");
		}

		projectService.update(project);
	}

	/**
	 * Synchronize a given {@link Sample} to the local installation.
	 *
	 * @param sample          the {@link Sample} to synchronize. This should have been read
	 *                        from a remote api.
	 * @param project         The {@link Project} the {@link Sample} belongs in.
	 * @param existingSamples A map of samples that have already been synchronized.  These will be checked to see if they've been updated
	 * @return A list of {@link ProjectSynchronizationException}s, empty if no errors.
	 */
	public List<ProjectSynchronizationException> syncSample(Sample sample, Project project, Map<String, Sample> existingSamples) {
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
			projectService.addSampleToProject(project, sample, true);
		}

		// get the local files and organize by their url
		
		Collection<SampleSequencingObjectJoin> localObjects = objectService.getSequencingObjectsForSample(localSample);

		Map<String, SequencingObject> objectsByUrl = new HashMap<>();
		localObjects.forEach(j -> {
			SequencingObject pair = j.getObject();
			
			// check if the file was actually sync'd. Someone may have
			// concatenated it
			if (pair.getRemoteStatus() != null) {
				String url = pair.getRemoteStatus().getURL();

				objectsByUrl.put(url, pair);
			}
		});

		List<SequenceFilePair> sequenceFilePairsForSample = pairRemoteService.getSequenceFilePairsForSample(sample);
		
		List<ProjectSynchronizationException> syncErrors = new ArrayList<>();

		for (SequenceFilePair pair : sequenceFilePairsForSample) {
			if (!objectsByUrl.containsKey(pair.getRemoteStatus().getURL())) {
				pair.setId(null);
				try {
					syncSequenceFilePair(pair, localSample);
				} catch (ProjectSynchronizationException e) {
					syncErrors.add(e);
				}
			}
		}

		List<SingleEndSequenceFile> unpairedFilesForSample = singleEndRemoteService.getUnpairedFilesForSample(sample);

		for (SingleEndSequenceFile file : unpairedFilesForSample) {
			if (!objectsByUrl.containsKey(file.getRemoteStatus().getURL())) {
				file.setId(null);
				try {
					syncSingleEndSequenceFile(file, localSample);
				} catch (ProjectSynchronizationException e) {
					syncErrors.add(e);
				}
			}
		}

		if (syncErrors.isEmpty()) {
			localSample.getRemoteStatus().setSyncStatus(SyncStatus.SYNCHRONIZED);
		} else {
			localSample.getRemoteStatus().setSyncStatus(SyncStatus.ERROR);

			logger.error(
					"Setting sample " + localSample.getId() + " sync status to ERROR due to sync errors with files");
		}

		sampleService.update(localSample);

		return syncErrors;
	}

	/**
	 * Synchronize the given sample's metadata
	 *
	 * @param sample the sample to sync
	 * @return the synchronized sample
	 */
	public Sample syncSampleMetadata(Sample sample){
		Map<String, MetadataEntry> sampleMetadata = sampleRemoteService.getSampleMetadata(sample);
		
		sampleMetadata.values().forEach(e -> e.setId(null));
		
		Set<MetadataEntry> metadata = metadataTemplateService.getMetadataSet(sampleMetadata);
		sample.setMetadataEntries(metadata);
		
		return sample;
	}

	/**
	 * Synchronize a given {@link SingleEndSequenceFile} to the local
	 * installation
	 * 
	 * @param file
	 *            the {@link SingleEndSequenceFile} to sync
	 * @param sample
	 *            the {@link Sample} to add the file to
	 */
	public void syncSingleEndSequenceFile(SingleEndSequenceFile file, Sample sample) {
		RemoteStatus fileStatus = file.getRemoteStatus();
		fileStatus.setSyncStatus(SyncStatus.UPDATING);
		try {
			file = singleEndRemoteService.mirrorSequencingObject(file);

			file.setProcessingState(SequencingObject.ProcessingState.UNPROCESSED);
			file.setFileProcessor(null);

			file.getSequenceFile().setId(null);
			file.getSequenceFile().getRemoteStatus().setSyncStatus(SyncStatus.SYNCHRONIZED);

			objectService.createSequencingObjectInSample(file, sample);

			fileStatus.setSyncStatus(SyncStatus.SYNCHRONIZED);

			objectService.updateRemoteStatus(file.getId(), fileStatus);
		} catch (Exception e) {
			logger.error("Error transferring file: " + file.getRemoteStatus().getURL(), e);
			throw new ProjectSynchronizationException("Could not synchronize file " + file.getRemoteStatus().getURL(),
					e);
		}
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
		try {
			pair = pairRemoteService.mirrorSequencingObject(pair);

			pair.setProcessingState(SequencingObject.ProcessingState.UNPROCESSED);
			pair.setFileProcessor(null);

			pair.getFiles().forEach(s -> {
				s.setId(null);
				s.getRemoteStatus().setSyncStatus(SyncStatus.SYNCHRONIZED);
			});

			objectService.createSequencingObjectInSample(pair, sample);

			RemoteStatus pairStatus = pair.getRemoteStatus();
			pairStatus.setSyncStatus(SyncStatus.SYNCHRONIZED);

			objectService.updateRemoteStatus(pair.getId(), pairStatus);
		} catch (Exception e) {
			logger.error("Error transferring file: " + pair.getRemoteStatus().getURL(), e);
			throw new ProjectSynchronizationException("Could not synchronize pair " + pair.getRemoteStatus().getURL(),
					e);
		}
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
	 * @param user
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
