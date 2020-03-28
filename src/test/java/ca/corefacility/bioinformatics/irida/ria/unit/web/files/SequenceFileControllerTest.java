package ca.corefacility.bioinformatics.irida.ria.unit.web.files;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
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
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.LocalSequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.ria.web.files.SequenceFileController;
import ca.corefacility.bioinformatics.irida.service.AnalysisService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;

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

	// Services
	private SequencingRunService sequencingRunService;
	private SequencingObjectService objectService;
	private AnalysisService analysisService;

	@Before
	public void setUp() {
		sequencingRunService = mock(SequencingRunService.class);
		analysisService = mock(AnalysisService.class);
		objectService = mock(SequencingObjectService.class);
		controller = new SequenceFileController(objectService, sequencingRunService, analysisService);

		Path path = Paths.get(FILE_PATH);
		SequenceFile file = new LocalSequenceFile(path);
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
		Model model = new ExtendedModelMap();

		String response = controller.getSequenceFilePage(model, OBJECT_ID, FILE_ID);
		assertEquals("Should return the correct page", SequenceFileController.FILE_DETAIL_PAGE, response);
		testModel(model);
	}

	@Test
	public void testGetSequenceFileOverrepresentedPage() {
		logger.debug("Testing getSequenceFilePage");
		Model model = new ExtendedModelMap();
		String response = controller.getSequenceFileOverrepresentedPage(model, OBJECT_ID, FILE_ID);
		assertEquals("Should return the correct page", SequenceFileController.FILE_OVERREPRESENTED, response);
		testModel(model);
	}

	/***********************************************************************************************
	 * AJAX TESTS
	 ***********************************************************************************************/

	@Test
	public void testDownloadSequenceFile() throws IOException {
		logger.debug("Testing downloadSequenceFile");
		MockHttpServletResponse response = new MockHttpServletResponse();

		controller.downloadSequenceFile(OBJECT_ID, FILE_ID, response);
		assertTrue("Response should contain a \"Content-Disposition\" header.",
				response.containsHeader("Content-Disposition"));
		assertEquals("Content-Disposition should include the file name", "attachment; filename=\"test_file.fastq\"",
				response.getHeader("Content-Disposition"));

		Path path = Paths.get(FILE_PATH);
		byte[] origBytes = Files.readAllBytes(path);
		byte[] responseBytes = response.getContentAsByteArray();
		assertArrayEquals("Response contents the correct file content", origBytes, responseBytes);
	}

	private void testModel(Model model) {
		assertTrue("Model should contain information about the file.", model.containsAttribute("file"));
		assertTrue("Model should contain the created date for the file.", model.containsAttribute("created"));
		assertTrue("Model should contain the fastQC data for the file.", model.containsAttribute("fastQC"));
		assertTrue("Model should contain the active nav id", model.containsAttribute(SequenceFileController.ACTIVE_NAV));
	}
}
