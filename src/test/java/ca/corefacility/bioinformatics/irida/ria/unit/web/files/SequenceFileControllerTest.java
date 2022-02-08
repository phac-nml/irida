package ca.corefacility.bioinformatics.irida.ria.unit.web.files;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletResponse;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;

import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageLocalUtilityImpl;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageUtility;
import ca.corefacility.bioinformatics.irida.ria.web.files.SequenceFileController;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.util.IridaFiles;

/**
 * Unit Tests for @{link SequenceFileController}
 *
 */
public class SequenceFileControllerTest {
	public static final Long FILE_ID = 1L;
	public static final Long OBJECT_ID = 2L;
	public static final String FILE_PATH = "src/test/resources/files/test_file.fastq";
	private static final Logger logger = LoggerFactory.getLogger(SequenceFileControllerTest.class);
	private SequenceFileController controller;
	private IridaFileStorageUtility iridaFileStorageUtility;

	// Services
	private SequencingObjectService objectService;

	@BeforeEach
	public void setUp() {
		objectService = mock(SequencingObjectService.class);
		controller = new SequenceFileController(objectService);
		iridaFileStorageUtility = new IridaFileStorageLocalUtilityImpl();
		IridaFiles.setIridaFileStorageUtility(iridaFileStorageUtility);

		Path path = Paths.get(FILE_PATH);
		SequenceFile file = new SequenceFile(path);
		file.setId(FILE_ID);
		SingleEndSequenceFile seqObject = new SingleEndSequenceFile(file);
		when(objectService.read(anyLong())).thenReturn(seqObject);
	}

	/**
	 *********************************************************************************************
	 * PAGE TESTS
	 *********************************************************************************************
	 */
	@Test
	public void testGetSequenceFilePage() {
		logger.debug("Testing getSequenceFilePage");
		String response = controller.getSequenceFilePage(OBJECT_ID, FILE_ID);
		assertEquals(SequenceFileController.FASTQC_PAGE, response, "Should return the correct page");
	}

	/***********************************************************************************************
	 * AJAX TESTS
	 ***********************************************************************************************/

	@Test
	public void testDownloadSequenceFile() throws IOException {
		logger.debug("Testing downloadSequenceFile");
		MockHttpServletResponse response = new MockHttpServletResponse();

		controller.downloadSequenceFile(OBJECT_ID, FILE_ID, response);
		assertTrue(response.containsHeader("Content-Disposition"),
				"Response should contain a \"Content-Disposition\" header.");
		assertEquals("attachment; filename=\"test_file.fastq\"", response.getHeader("Content-Disposition"),
				"Content-Disposition should include the file name");

		Path path = Paths.get(FILE_PATH);
		byte[] origBytes = Files.readAllBytes(path);
		byte[] responseBytes = response.getContentAsByteArray();
		assertArrayEquals(origBytes, responseBytes, "Response contents the correct file content");
	}

}
