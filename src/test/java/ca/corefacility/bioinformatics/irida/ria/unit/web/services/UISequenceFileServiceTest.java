package ca.corefacility.bioinformatics.irida.ria.unit.web.services;


import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;

import ca.corefacility.bioinformatics.irida.ria.web.files.dto.FastQCDetailsResponse;

import ca.corefacility.bioinformatics.irida.ria.web.files.dto.FastQCImagesResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UISequenceFileService;
import ca.corefacility.bioinformatics.irida.service.AnalysisService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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

	@Before
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
		assertEquals("Sequence file has correct id", fastQCDetailsResponse.getSequenceFile().getId(), FILE_ID);
		assertEquals("Sequence file has correct path", fastQCDetailsResponse.getSequenceFile().getFile().toString(), FILE_PATH);
		assertTrue("Has a sequencing object", seqObject != null);
		assertTrue("Has a an AnalysisFastQC object", fastQCDetailsResponse.getAnalysisFastQC() != null);
		assertTrue("Response type is FastQCDetailsResponse", fastQCDetailsResponse.getClass().equals(FastQCDetailsResponse.class));
	}

	@Test
	public void testGetFastQCImages() throws IOException {
		FastQCImagesResponse response = service.getFastQCCharts(OBJECT_ID, FILE_ID);
		verify(sequencingObjectService, times(1)).read(OBJECT_ID);
		verify(analysisService, times(1)).getFastQCAnalysisForSequenceFile(seqObject, FILE_ID);
		assertTrue("Response type is FastQCImagesResponse", response.getClass().equals(FastQCImagesResponse.class));
	}

	@Test
	public void testOverRepresentedSequences() {
		AnalysisFastQC analysisFastQC = service.getOverRepresentedSequences(OBJECT_ID, FILE_ID);
		verify(sequencingObjectService, times(1)).read(OBJECT_ID);
		verify(analysisService, times(1)).getFastQCAnalysisForSequenceFile(seqObject, FILE_ID);
		assertTrue("Has a an AnalysisFastQC object", analysisFastQC != null);
	}
}
