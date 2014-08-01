package ca.corefacility.bioinformatics.irida.ria.unit.web.files;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.ria.web.files.SequenceFileController;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;

/**
 * Unit Tests for @{link SequenceFileController}
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class SequenceFileControllerTest {
	public static final Long FILE_ID = 1l;
	public static final String FILE_PATH = "src/test/resources/files/test_file.fastq";
	private static final Logger logger = LoggerFactory.getLogger(SequenceFileControllerTest.class);
	private SequenceFileController controller;

	// Services
	private SequenceFileService sequenceFileService;

	@Before
	public void setUp() {
		sequenceFileService = mock(SequenceFileService.class);
		controller = new SequenceFileController(sequenceFileService);
	}

	@Test
	public void testGetSequenceFilePage() {
		logger.debug("Testing getSequenceFilePage");
		Model model = new ExtendedModelMap();
		String response = controller.getSequenceFilePage(model, FILE_ID);
		assertTrue("Model should contain information about the file.", model.containsAttribute("file"));
		assertEquals("Should return the correct page", SequenceFileController.FILE_DETAIL_PAGE, response);
	}

	@Test
	public void testDownloadSequenceFile() throws IOException {
		logger.debug("Testing downloadSequenceFile");
		Path path = Paths.get(FILE_PATH);
		SequenceFile file = new SequenceFile(path);
		MockHttpServletResponse response = new MockHttpServletResponse();

		when(sequenceFileService.read(FILE_ID)).thenReturn(file);
		controller.downloadSequenceFile(FILE_ID, response);
		assertTrue("Response should contain a \"Content-Disposition\" header.",
				response.containsHeader("Content-Disposition"));
		assertEquals("Content-Disposition should include the file name", "attachment; filename=\"test_file.fastq\"",
				response.getHeader("Content-Disposition"));

		byte[] origBytes = Files.readAllBytes(path);
		byte[] responseBytes = response.getContentAsByteArray();
		assertArrayEquals("Response contents the correct file content", origBytes, responseBytes);
	}
}
