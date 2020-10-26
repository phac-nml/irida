package ca.corefacility.bioinformatics.irida.ria.unit.web.files;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;
import ca.corefacility.bioinformatics.irida.ria.web.files.SequenceFileAjaxController;
import ca.corefacility.bioinformatics.irida.ria.web.files.dto.FastQCDetailsResponse;
import ca.corefacility.bioinformatics.irida.ria.web.files.dto.FastQCImagesResponse;
import ca.corefacility.bioinformatics.irida.service.AnalysisService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class SequenceFileAjaxControllerTest {
	public static final Long FILE_ID = 1L;
	public static final Long OBJECT_ID = 2L;
	public static final String FILE_PATH = "src/test/resources/files/test_file.fastq";
	private static final Logger logger = LoggerFactory.getLogger(SequenceFileControllerTest.class);

	private AnalysisService analysisService;
	private SequencingObjectService sequencingObjectService;
	private SequenceFileAjaxController sequenceFileAjaxController;
	private AnalysisFastQC fastQC;
	private SingleEndSequenceFile seqObject;
	@Before
	public void setUp() {
		analysisService = mock(AnalysisService.class);
		sequencingObjectService = mock(SequencingObjectService.class);
		sequenceFileAjaxController = new SequenceFileAjaxController(analysisService, sequencingObjectService);
		fastQC = mock(AnalysisFastQC.class);

		Path path = Paths.get(FILE_PATH);
		SequenceFile file = new SequenceFile(path);
		file.setId(FILE_ID);
		seqObject = new SingleEndSequenceFile(file);
		when(sequencingObjectService.read(anyLong())).thenReturn(seqObject);
		when(analysisService.getFastQCAnalysisForSequenceFile(seqObject, file.getId())).thenReturn(fastQC);
	}

	/**
	 *********************************************************************************************
	 * PAGE TESTS
	 *********************************************************************************************
	 */
	@Test
	public void testGetFastQCDetails() {
		ResponseEntity<FastQCDetailsResponse> response = sequenceFileAjaxController.getFastQCDetails(OBJECT_ID, FILE_ID);
		verify(sequencingObjectService, times(1)).read(OBJECT_ID);
		verify(analysisService, times(1)).getFastQCAnalysisForSequenceFile(seqObject, FILE_ID);
		assertEquals("Sequence file has correct id", response.getBody().getSequenceFile().getId(), FILE_ID);
		assertEquals("Sequence file has correct path", response.getBody().getSequenceFile().getFile().toString(), FILE_PATH);
		assertTrue("Has a sequencing object", seqObject != null);
		assertTrue("Has a an AnalysisFastQC object", response.getBody().getAnalysisFastQC() != null);
		assertTrue("Response type is FastQCDetailsResponse", response.getBody().getClass().equals(FastQCDetailsResponse.class));
	}

	@Test
	public void testGetFastQCImages() throws IOException {
		ResponseEntity<FastQCImagesResponse> response = sequenceFileAjaxController.getFastQCCharts(OBJECT_ID, FILE_ID);
		verify(sequencingObjectService, times(1)).read(OBJECT_ID);
		verify(analysisService, times(1)).getFastQCAnalysisForSequenceFile(seqObject, FILE_ID);
		assertTrue("Response type is FastQCImagesResponse", response.getBody().getClass().equals(FastQCImagesResponse.class));
	}

	@Test
	public void testOverRepresentedSequences() {
		ResponseEntity<AnalysisFastQC> response = sequenceFileAjaxController.getOverRepresentedSequences(OBJECT_ID, FILE_ID);
		verify(sequencingObjectService, times(1)).read(OBJECT_ID);
		verify(analysisService, times(1)).getFastQCAnalysisForSequenceFile(seqObject, FILE_ID);
		assertTrue("Has a an AnalysisFastQC object", response.getBody() != null);
	}

}
