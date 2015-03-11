package ca.corefacility.bioinformatics.irida.service.upload;

import java.net.URL;
import java.util.Set;

import javax.validation.ConstraintViolationException;

import org.springframework.security.access.prepost.PreAuthorize;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.upload.UploadProjectName;
import ca.corefacility.bioinformatics.irida.model.upload.UploaderAccountName;
import ca.corefacility.bioinformatics.irida.pipeline.upload.UploadWorker;

/**
 * Service for performing uploads of genomic sequence data to a remote site.
 * 
 *
 * @param <ProjectName>
 *            The name of the project to upload into.
 * @param <AccountName>
 *            The name of the user account to make an owner of a new data
 *            location.
 */
public interface UploadService<ProjectName extends UploadProjectName, AccountName extends UploaderAccountName> {
	/**
	 * Performs the upload for all samples into Galaxy from this project.
	 *
	 * @param projectId
	 *            The project id to upload samples to.
	 * @param projectName
	 *            The name of the project to upload into.
	 * @param accountName
	 *            The name of the account to upload into.
	 * @return An UploadWorker with information on the current progress of the
	 *         upload.
	 * @throws ConstraintViolationException
	 *             If the upload information fails to match the constraints.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#projectId, 'canReadProject')")
	public UploadWorker performUploadAllSamples(long projectId,
			ProjectName projectName, AccountName accountName)
			throws ConstraintViolationException;

	/**
	 * Performs an upload for the selected samples into Galaxy from this
	 * project.
	 *
	 * @param selectedSamples
	 *            The samples to upload.
	 * @param projectName
	 *            The name of the project to upload into.
	 * @param accountName
	 *            The name of the account to upload into.
	 * @return An UploadWorker for with information on the progress of the
	 *         upload.
	 * @throws ConstraintViolationException
	 *             If the upload information fails to match the constraints.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#selectedSamples, 'canReadSample')")
	public UploadWorker performUploadSelectedSamples(
			Set<Sample> selectedSamples, ProjectName projectName,
			AccountName accountName) throws ConstraintViolationException;

	/**
	 * Performs an upload for the selected sequence files into Galaxy.
	 * 
	 * @param selectedSequenceFileIds
	 *            The ids of the sequence files to upload to Galaxy.
	 * @param projectName
	 *            The name of the project to upload into.
	 * @param accountName
	 *            The name of the account to upload into.
	 * @return An UploadWorker for with information on the progress of the
	 *         upload.
	 * @throws ConstraintViolationException
	 *             If the upload information fails to match the constraints.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#selectedSequenceFileIds, 'canReadSequenceFile')")
	public UploadWorker performUploadSelectedSequenceFiles(
			Set<Long> selectedSequenceFileIds, ProjectName projectName,
			AccountName accountName) throws ConstraintViolationException;
	
	/**
	 * Performs an upload for the selected sequence files into Galaxy.
	 * 
	 * @param selectedSequenceFileIds
	 *            The ids of the sequence files to upload to Galaxy.
	 * @param projectName
	 *            The name of the project to upload into.
	 * @param accountName
	 *            The name of the account to upload into.
	 * @return An UploadWorker for with information on the progress of the
	 *         upload.
	 * @throws ConstraintViolationException
	 *             If the upload information fails to match the constraints.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#selectedSequenceFileIds, 'canReadSequenceFile')")
	public UploadWorker performUploadSelectedSequenceFiles(
			Set<Long> selectedSequenceFileIds, String projectName,
			String accountName) throws ConstraintViolationException;

	/**
	 * Gets the URL of the connected remote site.
	 * 
	 * @return The URL of the connected remote site.
	 */
	public URL getUrl();

	/**
	 * Check if this service is configured to upload to a remote site.
	 * 
	 * @return True if this service is configured to upload to a remote site,
	 *         false otherwise.
	 */
	public boolean isConfigured();

	/**
	 * Check if this service is connected to a running instance of the remote
	 * site.
	 * 
	 * @return True if this service is connected to a running instance of a
	 *         remote site, false otherwise.
	 */
	public boolean isConnected();
}
