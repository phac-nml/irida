package ca.corefacility.bioinformatics.irida.ria.integration.cloud;

import java.io.IOException;

/**
 * Implementation of file utilities for storage testing. All file utility testing files (azure, aws, etc) must implement
 * this interface
 */

public interface IridaFileStorageTestUtility {

	public void testGetTemporaryFile();

	public void testGetTemporaryFileWithPrefix();

	public void testCleanupDownloadedLocalTemporaryFiles();

	public void testWriteFile() throws IOException;

	public void testDeleteFile();

	public void testGetFileName();

	public void testFileExists();

	public void testGetFileInputStream() throws IOException;

	public void testIsGzipped() throws IOException;

	public void testAppendToFile() throws IOException;

	public void testGetFileExtension() throws IOException;

	public void testReadAllBytes() throws IOException;

	public void testGetFileSizeBytes();

	public void testReadChunk();

	public void testCheckWriteAccess();

	public void testGetStorageType();
}
