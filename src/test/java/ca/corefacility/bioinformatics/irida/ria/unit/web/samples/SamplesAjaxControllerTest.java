package ca.corefacility.bioinformatics.irida.ria.unit.web.samples;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.ria.unit.TestDataFactory;
import ca.corefacility.bioinformatics.irida.ria.web.samples.SamplesAjaxController;
import ca.corefacility.bioinformatics.irida.ria.web.services.UISampleService;
import ca.corefacility.bioinformatics.irida.service.GenomeAssemblyService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.collect.ImmutableList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class SamplesAjaxControllerTest {
	private SamplesAjaxController controller;
	private SequencingObjectService sequencingObjectService;
	private GenomeAssemblyService genomeAssemblyService;
	private UISampleService uiSampleService;

	/*
	TEST DATA
	 */
	private final Sample SAMPLE = TestDataFactory.constructSample();
	private final String FILE_01 = "test_file_A.fastq";
	private final String FILE_02 = "test_file_B.fastq";
	private final String PAIR_01 = "pair_test_R1_001.fastq";
	private final String PAIR_02 = "pair_test_R2_001.fastq";
	private final List<String> SINGLE_FILE_NAMES = ImmutableList.of(FILE_01, FILE_02);
	private final List<String> PAIRED_FILE_NAMES = ImmutableList.of(PAIR_01, PAIR_02);
	private final List<String> MIXED_FILE_NAMES = ImmutableList.of(FILE_01, PAIR_01, PAIR_02);
	MockMultipartFile MOCK_FILE_01;
	MockMultipartFile MOCK_FILE_02;
	MockMultipartFile MOCK_PAIR_FILE_01;
	MockMultipartFile MOCK_PAIR_FILE_02;

	@BeforeEach
	public void setUp() {
		SampleService sampleService = mock(SampleService.class);
		sequencingObjectService = mock(SequencingObjectService.class);
		genomeAssemblyService = mock(GenomeAssemblyService.class);
		uiSampleService = mock(UISampleService.class);
		MessageSource messageSource = mock(MessageSource.class);
		controller = new SamplesAjaxController(sampleService, sequencingObjectService, genomeAssemblyService,
				uiSampleService, messageSource);

		// Set up mocks
		when(sampleService.read(SAMPLE.getId())).thenReturn(SAMPLE);
		MOCK_FILE_01 = createMultiPartFile(FILE_01, "src/test/resources/files/test_file_A.fastq");
		MOCK_FILE_02 = createMultiPartFile(FILE_02, "src/test/resources/files/test_file_B.fastq");
		MOCK_PAIR_FILE_01 = createMultiPartFile(PAIR_01, "src/test/resources/files/pairs/pair_test_R1_001.fastq");
		MOCK_PAIR_FILE_02 = createMultiPartFile(PAIR_02,  "src/test/resources/files/pairs/pair_test_R2_001.fastq");
	}

	private MockMultipartFile createMultiPartFile(String name, String path) {
		try {
			FileInputStream fis = new FileInputStream(path);
			String contents =  IOUtils.toString(fis, "UTF-8");
			return new MockMultipartFile(name, name, "text/plain", contents.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Test
	public void testUploadSequenceFiles() {
		MultipartHttpServletRequest request = mock(MultipartHttpServletRequest.class);
		when(request.getFile(FILE_01)).thenReturn(MOCK_FILE_01);
		when(request.getFile(FILE_02)).thenReturn(MOCK_FILE_02);
		when(request.getFileNames()).thenReturn(SINGLE_FILE_NAMES.iterator());
		ArgumentCaptor<SingleEndSequenceFile> sequenceFileArgumentCaptor = ArgumentCaptor
				.forClass(SingleEndSequenceFile.class);

		ResponseEntity<String> responseEntity = controller.uploadSequenceFiles(SAMPLE.getId(), request, Locale.CANADA);

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "Response is ok");
		verify(sequencingObjectService, times(2)).createSequencingObjectInSample(sequenceFileArgumentCaptor.capture(),
				eq(SAMPLE));
		assertEquals(FILE_02, sequenceFileArgumentCaptor.getValue().getLabel(),
				"Should have the correct file name");
	}
	@Test
	public void testUploadSequenceFilePairs(){
		MultipartHttpServletRequest request = mock(MultipartHttpServletRequest.class);
		when(request.getFile(PAIR_01)).thenReturn(MOCK_PAIR_FILE_01);
		when(request.getFile(PAIR_02)).thenReturn(MOCK_PAIR_FILE_02);
		when(request.getFileNames()).thenReturn(PAIRED_FILE_NAMES.iterator());
		ArgumentCaptor<SequenceFilePair> sequenceFileArgumentCaptor = ArgumentCaptor.forClass(SequenceFilePair.class);
		ResponseEntity<String> responseEntity = controller.uploadSequenceFiles(SAMPLE.getId(), request, Locale.CANADA);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "Response is ok");

		verify(sequencingObjectService)
				.createSequencingObjectInSample(sequenceFileArgumentCaptor.capture(), eq(SAMPLE));

		assertEquals(PAIR_01, sequenceFileArgumentCaptor.getValue().getForwardSequenceFile().getLabel(),
				"Should have the correct file name");
		assertEquals(PAIR_02, sequenceFileArgumentCaptor.getValue().getReverseSequenceFile().getLabel(),
				"Should have the correct file name");
	}


	@Test
	public void testUploadSequenceFilePairsAndSingle() {
		MultipartHttpServletRequest request = mock(MultipartHttpServletRequest.class);
		when(request.getFile(FILE_01)).thenReturn(MOCK_FILE_01);
		when(request.getFile(PAIR_01)).thenReturn(MOCK_PAIR_FILE_01);
		when(request.getFile(PAIR_02)).thenReturn(MOCK_PAIR_FILE_02);
		when(request.getFileNames()).thenReturn(MIXED_FILE_NAMES.iterator());
		ArgumentCaptor<SequencingObject> sequenceFileArgumentCaptor = ArgumentCaptor.forClass(SequencingObject.class);
		ResponseEntity<String> responseEntity = controller.uploadSequenceFiles(SAMPLE.getId(), request, Locale.CANADA);

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "Response is ok");
		verify(sequencingObjectService, times(2)).createSequencingObjectInSample(sequenceFileArgumentCaptor.capture(),
				eq(SAMPLE));

		List<SequencingObject> allValues = sequenceFileArgumentCaptor.getAllValues();

		assertEquals(1, allValues.stream().filter(o -> o instanceof SingleEndSequenceFile).count(),
				"Should have created 1 single end sequence files");
		assertEquals(1, allValues.stream().filter(o -> o instanceof SequenceFilePair).count(),
				"Should have created 1 file pair");
	}
}
