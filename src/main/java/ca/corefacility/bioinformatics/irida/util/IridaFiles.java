package ca.corefacility.bioinformatics.irida.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageUtility;

/**
 * Static class which has file object operations that require
 * access to the iridaFileStorageUtility but in a static context
 */

public final class IridaFiles {

	private static IridaFileStorageUtility iridaFileStorageUtility;

	public static void setIridaFileStorageUtility(IridaFileStorageUtility iridaFileStorageUtility) {
		IridaFiles.iridaFileStorageUtility = iridaFileStorageUtility;
	}

	private IridaFiles() {
	}

	/**
	 * Gets the file size of the file from the iridaFileStorageUtility
	 * and returns it as a human readable string
	 *
	 * @param file The path to the file
	 * @return file size as a human readable string
	 */
	public static String getFileSize(Path file) {
		String fileSize = "N/A";
		Long fileSizeBytes = iridaFileStorageUtility.getFileSizeBytes(file);

		if (fileSizeBytes > 0) {
			fileSize = FileUtils.humanReadableByteCount(fileSizeBytes, true);
		}
		return fileSize;
	}

	/**
	 * Checks if the file is gzipped in iridaFileStorageUtility
	 *
	 * @param file The path to the file
	 * @return if file is gzipped or not
	 * @throws IOException if file cannot be read
	 */
	public static boolean isGzipped(Path file) throws IOException {
		return iridaFileStorageUtility.isGzipped(file);
	}

	/**
	 * Gets the file input stream from iridaFileStorageUtility
	 *
	 * @param file The path to the file
	 * @return the file input stream
	 */
	public static InputStream getFileInputStream(Path file) {
		return iridaFileStorageUtility.getFileInputStream(file);
	}

	/**
	 * Gets the file extension from iridaFileStorageUtility
	 *
	 * @param files List of sequencingObjects to get file extensions for
	 * @return the common extension of the files
	 * @throws IOException if file(s) cannot be read
	 */
	public static String getFileExtension(List<? extends SequencingObject> files) throws IOException {
		return iridaFileStorageUtility.getFileExtension(files);
	}

	/**
	 * Get the bytes for a file
	 *
	 * @param file The path to the file
	 * @return the bytes for the file
	 * @throws IOException if the file couldn't be read
	 */
	public static byte[] getBytesForFile(Path file) throws IOException {
		byte[] bytes = iridaFileStorageUtility.readAllBytes(file);
		return bytes;
	}

	/**
	 * Gets the file size in bytes of the file from the iridaFileStorageUtility
	 *
	 * @param file The path to the file
	 * @return file size in bytes
	 */
	public static Long getFileSizeBytes(Path file) {
		return iridaFileStorageUtility.getFileSizeBytes(file);
	}

	/**
	 * Checks if the file exists in iridaFileStorageUtility
	 *
	 * @param file The path to the file
	 * @return if file exists or not
	 */
	public static boolean fileExists(Path file) {
		return iridaFileStorageUtility.fileExists(file);
	}
}
