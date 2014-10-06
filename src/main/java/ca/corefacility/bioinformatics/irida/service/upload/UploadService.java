package ca.corefacility.bioinformatics.irida.service.upload;

import java.net.URL;
import java.util.Set;

import javax.validation.ConstraintViolationException;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.upload.UploadProjectName;
import ca.corefacility.bioinformatics.irida.model.upload.UploaderAccountName;
import ca.corefacility.bioinformatics.irida.pipeline.upload.UploadWorker;

/**
 * Service for performing uploads of genomic sequence data to a remote site.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 * @param <ProjectName>  The name of the project to upload into.
 * @param <AccountName>  The name of the user account to make an owner of a new data location.
 */
public interface UploadService<ProjectName extends UploadProjectName, AccountName extends UploaderAccountName> {
	/**
	 * Builds an UploadWorker used to upload all samples into Galaxy from this
	 * project.
	 *
	 * @param projectId
	 *            The project id to upload samples to.
	 * @param projectName
	 *            The name of the project to upload into.
	 * @param accountName
	 *            The name of the account to upload into.
	 * @return An UploadWorker to be used for uploading files to a remote site..
	 * @throws ConstraintViolationException
	 *             If the upload information fails to match the constraints.
	 */
	public UploadWorker buildUploadWorkerAllSamples(long projectId,
			ProjectName projectName, AccountName accountName)
			throws ConstraintViolationException;

	/**
	 * Builds an UploadWorker used to upload the selected samples into Galaxy
	 * from this project.
	 *
	 * @param selectedSamples
	 *            The samples to upload.
	 * @param projectName
	 *            The name of the project to upload into.
	 * @param accountName
	 *            The name of the account to upload into.
	 * @return An UploadWorker for uploading files into a remote site.
	 * @throws ConstraintViolationException
	 *             If the upload information fails to match the constraints.
	 */
	public UploadWorker buildUploadWorkerSelectedSamples(
			Set<Sample> selectedSamples, ProjectName projectName,
			AccountName accountName) throws ConstraintViolationException;

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
