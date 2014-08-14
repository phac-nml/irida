package ca.corefacility.bioinformatics.irida.ria.unit.web.files;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletResponse;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.ria.web.files.ReferenceFileController;
import ca.corefacility.bioinformatics.irida.service.ReferenceFileService;

/**
 * Unit Tests for @{link ReferenceFileController}
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class ReferenceFileControllerTest {
	private static final Logger logger = LoggerFactory.getLogger(ReferenceFileControllerTest.class);

	// Constants
	public static final Long FILE_ID = 1l;
	public static final Long BAD_FILE_ID = 2l;
	public static final String FILE_PATH = "src/test/resources/files/test_file.fastq";

	// Controller
	private ReferenceFileController controller;

	// Services
	private ReferenceFileService referenceFileService;

	@Before
	public void setUp() {
		referenceFileService = mock(ReferenceFileService.class);

		controller = new ReferenceFileController(referenceFileService);

		// Set up the reference file
		Path path = Paths.get(FILE_PATH);
		ReferenceFile file = new ReferenceFile(path);
		when(referenceFileService.read(FILE_ID)).thenReturn(file);
	}

	/***********************************************************************************************
	 * AJAX TESTS
	 ***********************************************************************************************/

	@Test
	public void testDownloadReferenceFile() throws IOException {
		logger.debug("Testing download reference file");
		MockHttpServletResponse response = new MockHttpServletResponse();

		controller.downloadReferenceFile(FILE_ID, response);
		assertTrue("Response should contain a \"Content-Disposition\" header.",
				response.containsHeader("Content-Disposition"));
		assertEquals("Content-Disposition should include the file name", "attachment; filename=\"test_file.fastq\"",
				response.getHeader("Content-Disposition"));

		Path path = Paths.get(FILE_PATH);
		byte[] origBytes = Files.readAllBytes(path);
		byte[] responseBytes = response.getContentAsByteArray();
		assertArrayEquals("Response contents the correct file content", origBytes, responseBytes);
	}

	@Test(expected = EntityNotFoundException.class)
	public void testBadDownloadReferenceFile() throws IOException {
		logger.debug("Testing download non-existent reference file");
		MockHttpServletResponse response = new MockHttpServletResponse();
		controller.downloadReferenceFile(BAD_FILE_ID, response);
	}
}
