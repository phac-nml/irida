package ca.corefacility.bioinformatics.irida.service.upload.galaxy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.upload.UploadSample;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
import ca.corefacility.bioinformatics.irida.pipeline.upload.UploadWorker;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyUploader;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.collect.Lists;

@Component
public class GalaxyUploadService {
	private static final Logger logger = LoggerFactory
			.getLogger(GalaxyUploadService.class);

	private ProjectService projectService;
	private SampleService sampleService;

	private UploadSampleConversionServiceGalaxy uploadSampleConversionService;

	private GalaxyUploader galaxyUploader;

	/**
	 * Builds a new GalaxyUploadService with the given information.
	 * 
	 * @param galaxyUploader
	 *            The GalaxyUploader to use to connect to an instance of Galaxy.
	 * @param projectService
	 *            The ProjectService for access to project information.
	 * @param sampleService
	 *            The SampleService for access to samples.
	 * @param uploadSampleConversionService
	 *            The service for constructing objects to upload to Galaxy.
	 */
	@Autowired
	public GalaxyUploadService(GalaxyUploader galaxyUploader,
			ProjectService projectService, SampleService sampleService,
			UploadSampleConversionServiceGalaxy uploadSampleConversionService) {
		this.galaxyUploader = galaxyUploader;

		this.projectService = projectService;
		this.sampleService = sampleService;
		this.uploadSampleConversionService = uploadSampleConversionService;
	}

	/**
	 * Gets the URL of the connected Galaxy instance.
	 * 
	 * @return The URL of the connected Galaxy instance.
	 */
	public URL getUrl() {
		return galaxyUploader.getUrl();
	}

	/**
	 * Get the samples for a specific project, identified by its project id.
	 *
	 * @param projectId
	 *            The project ID
	 * @return A list of GalaxySamples.
	 */
	private Set<UploadSample> getSamplesFor(Long projectId) {

		Set<UploadSample> galaxySamples = new HashSet<>();

		Project project = projectService.read(projectId);
		List<Join<Project, Sample>> sampleList = sampleService
				.getSamplesForProject(project);
		for (Join<Project, Sample> js : sampleList) {
			Sample sample = js.getObject();

			UploadSample galaxySample = uploadSampleConversionService
					.convertToUploadSample(sample);
			galaxySamples.add(galaxySample);
		}

		return galaxySamples;
	}

	/**
	 * Builds an UploadWorker used to upload all samples into Galaxy from this
	 * project.
	 *
	 * @param projectId
	 *            The project id to upload samples to.
	 * @param galaxyLibraryName
	 *            The library name to upload to.
	 * @param galaxyUserEmail
	 *            The Galaxy user account email to own the library.
	 * @return An UploadWorker to be used for uploading files into Galaxy.
	 * @throws ConstraintViolationException
	 *             If the upload information fails to match the constraints.
	 */
	public UploadWorker buildUploadWorkerAllSamples(long projectId,
			GalaxyProjectName galaxyLibraryName,
			GalaxyAccountEmail galaxyUserEmail)
			throws ConstraintViolationException {

		checkNotNull(galaxyLibraryName, "galaxyLibraryName is null");
		checkNotNull(galaxyUserEmail, "galaxyUserEmail is null");

		logger.debug("Uploading all samples for project id=" + projectId
				+ ", galaxy email=" + galaxyUserEmail + ", library name="
				+ galaxyLibraryName + " to Galaxy " + getUrl());

		Set<UploadSample> galaxySamples = getSamplesFor(projectId);

		return buildUploadWorker(galaxySamples, galaxyLibraryName,
				galaxyUserEmail);
	}

	private UploadWorker buildUploadWorker(Set<UploadSample> galaxySamples,
			GalaxyProjectName galaxyLibraryName,
			GalaxyAccountEmail galaxyUserEmail)
			throws ConstraintViolationException {

		return galaxyUploader.uploadSamples(Lists.newArrayList(galaxySamples),
				galaxyLibraryName, galaxyUserEmail);
	}

	/**
	 * Builds an UploadWorker used to upload the selected samples into Galaxy
	 * from this project.
	 *
	 * @param selectedSamples
	 *            The samples to upload.
	 * @param galaxyLibraryName
	 *            The library name in Galaxy to create.
	 * @param galaxyUserEmail
	 *            The user email in Galaxy who will own the data library.
	 * @return An UploadWorker for uploading files into Galaxy.
	 * @throws ConstraintViolationException
	 *             If the upload information fails to match the constraints.
	 */
	public UploadWorker buildUploadWorkerSelectedSamples(
			Set<Sample> selectedSamples, GalaxyProjectName galaxyLibraryName,
			GalaxyAccountEmail galaxyUserEmail)
			throws ConstraintViolationException {

		checkNotNull(selectedSamples, "selectedSamples is null");
		checkNotNull(galaxyLibraryName, "galaxyLibraryName is null");
		checkNotNull(galaxyUserEmail, "galaxyUserEmail is null");

		Set<UploadSample> galaxySamples = uploadSampleConversionService
				.convertToUploadSamples(selectedSamples);

		return buildUploadWorker(galaxySamples, galaxyLibraryName,
				galaxyUserEmail);
	}

	/**
	 * Check if this Galaxy controller is attached to a running instance of
	 * Galaxy.
	 * 
	 * @return True if the Galaxy controller is attached to a running instance
	 *         of Galaxy, false otherwise.
	 */
	public boolean isConfigured() {
		return galaxyUploader.isDataLocationAttached();
	}

	/**
	 * Check if this Galaxy controller is connected to a running instance of
	 * Galaxy.
	 * 
	 * @return True if the Galaxy controller is connected to a running instance
	 *         of Galaxy, false otherwise.
	 */
	public boolean isConnected() {
		return galaxyUploader.isDataLocationConnected();
	}
}
