package ca.corefacility.bioinformatics.irida.service;

import java.io.File;
import java.nio.file.Path;

import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import com.azure.storage.blob.models.BlobStorageException;

/**
 * Interface describing methods for performing storage actions
 */

public interface IridaFileStorageService {
	/**
	 * Get a temporarry file from storage
	 *
	 * @param file The {@link Path} to the file
	 * @return {@link File} which was retrieved from path
	 */
	public File getTemporaryFile(Path file);

	/**
	 * Get file size in bytes
	 *
	 * @param file The {@link Path} to the file
	 * @return {@link Long} size of file retrieved from path
	 */
	public Long getFileSize(Path file);

	/**
	 * Write file to storage (azure, aws, or local)
	 *
	 * @param source The {@link Path} to the file
	 * @param target The {@link Path} to where file should be moved
	 */
	public void writeFile(Path source, Path target);

	/**
	 * Returns if the storage type is local or not
	 *
	 * @return {@link Boolean#TRUE} if local, {@link Boolean#FALSE} if not.
	 */
	public boolean storageTypeIsLocal();

	/**
	 * Gets the file name from the storage type that the file
	 * is saved to (azure, aws, or local disk)
	 *
	 * @param file The path to the file for which to get name for
	 * @return {@link String} The file name for the file
	 */
	public String getFileName(Path file);

	/**
	 * Deletes the file from (azure, aws, or local disk)
	 *
	 */
	public void deleteFile();

	/**
	 * Download the file from (azure, aws, or local disk)
	 *
	 */
	public void downloadFile();

	/**
	 * Downloads all the files of type `analysis-output` from
	 * (azure, aws, or local disk)
	 *
	 */
	public void downloadFiles();
}
