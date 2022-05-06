package ca.corefacility.bioinformatics.irida.ria.unit.web.services;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;

import ca.corefacility.bioinformatics.irida.ria.web.files.dto.FastQCDetailsResponse;

import ca.corefacility.bioinformatics.irida.ria.web.files.dto.FastQCImagesResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UISequenceFileService;
import ca.corefacility.bioinformatics.irida.service.AnalysisService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UISequenceFileServiceTest {
	private UISequenceFileService service;
	private AnalysisService analysisService;
	private SequencingObjectService sequencingObjectService;

	private AnalysisFastQC fastQC;
	private SingleEndSequenceFile seqObject;

	public static final Long FILE_ID = 1L;
	public static final Long OBJECT_ID = 2L;
	public static final String FILE_PATH = "src/test/resources/files/test_file.fastq";

	@BeforeEach
	public void setUp() {
		analysisService = mock(AnalysisService.class);
		sequencingObjectService = mock(SequencingObjectService.class);
		service = new UISequenceFileService(analysisService, sequencingObjectService);
		fastQC = mock(AnalysisFastQC.class);

		Path path = Paths.get(FILE_PATH);
		SequenceFile file = new SequenceFile(path);
		file.setId(FILE_ID);
		seqObject = new SingleEndSequenceFile(file);
		when(sequencingObjectService.read(anyLong())).thenReturn(seqObject);
		when(analysisService.getFastQCAnalysisForSequenceFile(seqObject, file.getId())).thenReturn(fastQC);
	}

	@Test
	public void testGetDetails() {
		FastQCDetailsResponse fastQCDetailsResponse= service.getFastQCDetails(OBJECT_ID, FILE_ID);
		verify(sequencingObjectService, times(1)).read(OBJECT_ID);
		verify(analysisService, times(1)).getFastQCAnalysisForSequenceFile(seqObject, FILE_ID);
		assertEquals(fastQCDetailsResponse.getSequenceFile().getId(), FILE_ID, "Sequence file has correct id");
		assertEquals(fastQCDetailsResponse.getSequenceFile().getFile().toString(), FILE_PATH, "Sequence file has correct path");
		assertTrue(seqObject != null, "Has a sequencing object");
		assertTrue(fastQCDetailsResponse.getAnalysisFastQC() != null, "Has a an AnalysisFastQC object");
		assertTrue(fastQCDetailsResponse.getClass().equals(FastQCDetailsResponse.class), "Response type is FastQCDetailsResponse");
	}

	@Test
	public void testGetFastQCImages() throws IOException {
		FastQCImagesResponse response = service.getFastQCCharts(OBJECT_ID, FILE_ID);
		verify(sequencingObjectService, times(1)).read(OBJECT_ID);
		verify(analysisService, times(1)).getFastQCAnalysisForSequenceFile(seqObject, FILE_ID);
		assertTrue(response.getClass().equals(FastQCImagesResponse.class), "Response type is FastQCImagesResponse");
	}

	@Test
	public void testOverRepresentedSequences() {
		AnalysisFastQC analysisFastQC = service.getOverRepresentedSequences(OBJECT_ID, FILE_ID);
		verify(sequencingObjectService, times(1)).read(OBJECT_ID);
		verify(analysisService, times(1)).getFastQCAnalysisForSequenceFile(seqObject, FILE_ID);
		assertTrue(analysisFastQC != null, "Has a an AnalysisFastQC object");
	}

	@Test
	public void testDownloadSequenceFile() throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		service.downloadSequenceFile(OBJECT_ID, FILE_ID, response);
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
