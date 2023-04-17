package ca.corefacility.bioinformatics.irida.ria.unit.utilities;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageLocalUtilityImpl;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageUtility;
import ca.corefacility.bioinformatics.irida.ria.utilities.FileUtilities;
import ca.corefacility.bioinformatics.irida.util.IridaFiles;

import static org.junit.jupiter.api.Assertions.*;

public class FileUtilitiesTest {
	private IridaFileStorageUtility iridaFileStorageUtility;

	@BeforeEach
	public void setup() {
		iridaFileStorageUtility = new IridaFileStorageLocalUtilityImpl();
		IridaFiles.setIridaFileStorageUtility(iridaFileStorageUtility);
	}

	@Test
	public void testIsZippedFile() throws IOException {
		Path snpTreePath = Paths.get("src/test/resources/files/snp_tree.tree");
		Path zippedSnpTreePath = Paths.get("src/test/resources/files/snp_tree.tree.zip");

		boolean isZipped = FileUtilities.isZippedFile(snpTreePath);
		assertFalse(isZipped, "snp_tree.tree is not zipped");

		isZipped = FileUtilities.isZippedFile(zippedSnpTreePath);
		assertTrue(isZipped, "snp_tree.tree.zip is zipped");

		// File doesn't exist on local disk
		Path htmlFile = Paths.get("src/test/resources/files/test_html.html");

		assertThrows(StorageException.class, () -> {
			FileUtilities.isZippedFile(htmlFile);
		});
	}

	@Test
	public void testGetFileExt()  {
		// File doesn't exist on local disk
		Path fastqFile = Paths.get("src/test/resources/files/test_fastq.fastq");
		// File doesn't exist on local disk
		Path htmlFile = Paths.get("src/test/resources/files/test_html.html");
		// File exists on local disk
		Path htmlFileZipped = Paths.get("src/test/resources/files/test_html_zipped.html.zip");

		// Even though the file doesn't exist on disk we should still get the correct extension
		assertEquals("fastq", FileUtilities.getFileExt(fastqFile), "Extension should be fastq");
		assertEquals("html", FileUtilities.getFileExt(htmlFile), "Extension should be html");

		assertEquals("html-zip", FileUtilities.getFileExt(htmlFileZipped), "Extension should be html-zip");
	}
}
