package ca.corefacility.bioinformatics.irida.ria.unit.web.samples;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.samples.dto.SampleAnalyses;
import ca.corefacility.bioinformatics.irida.ria.web.samples.dto.SampleConcatenationModel;
import ca.corefacility.bioinformatics.irida.ria.web.samples.dto.SampleSequencingObjectFileModel;

import org.apache.commons.io.IOUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.unit.TestDataFactory;
import ca.corefacility.bioinformatics.irida.ria.web.samples.SamplesAjaxController;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIAnalysesService;
import ca.corefacility.bioinformatics.irida.ria.web.services.UISampleService;


import com.google.common.collect.ImmutableList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class SamplesAjaxControllerTest {
	private SamplesAjaxController controller;
	private UISampleService uiSampleService;
	private UIAnalysesService uiAnalysesService;

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
	private final Set<Long> sequencingObjectIds = Set.<Long>of(1L, 2L, 3L);
	Principal principal;

	@BeforeEach
	public void setUp() {
		uiSampleService = mock(UISampleService.class);
		uiAnalysesService = mock(UIAnalysesService.class);
		principal = mock(Principal.class);

		controller = new SamplesAjaxController(uiSampleService, uiAnalysesService);

		// Set up mocks
		//when(sampleService.read(SAMPLE.getId())).thenReturn(SAMPLE);
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

		ResponseEntity<List<SampleSequencingObjectFileModel>> responseEntity = controller.uploadSequenceFiles(SAMPLE.getId(), request);

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "Response is ok");
	}
	@Test
	public void testUploadSequenceFilePairs(){
		MultipartHttpServletRequest request = mock(MultipartHttpServletRequest.class);
		when(request.getFile(PAIR_01)).thenReturn(MOCK_PAIR_FILE_01);
		when(request.getFile(PAIR_02)).thenReturn(MOCK_PAIR_FILE_02);
		when(request.getFileNames()).thenReturn(PAIRED_FILE_NAMES.iterator());

		ResponseEntity<List<SampleSequencingObjectFileModel>> responseEntity = controller.uploadSequenceFiles(SAMPLE.getId(), request);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "Response is ok");
	}


	@Test
	public void testUploadSequenceFilePairsAndSingle() {
		MultipartHttpServletRequest request = mock(MultipartHttpServletRequest.class);
		when(request.getFile(FILE_01)).thenReturn(MOCK_FILE_01);
		when(request.getFile(PAIR_01)).thenReturn(MOCK_PAIR_FILE_01);
		when(request.getFile(PAIR_02)).thenReturn(MOCK_PAIR_FILE_02);
		when(request.getFileNames()).thenReturn(MIXED_FILE_NAMES.iterator());

		ResponseEntity<List<SampleSequencingObjectFileModel>> responseEntity = controller.uploadSequenceFiles(SAMPLE.getId(), request);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "Response is ok");

	}

	@Test
	public void testConcatenateFiles() {
		ResponseEntity<SampleConcatenationModel> responseEntity = controller.concatenateSequenceFiles(SAMPLE.getId(), sequencingObjectIds, "newFile", false, Locale.ENGLISH );
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "Response is ok");
	}

	@Test
	public void testUpdateDefaultSequencingObjectForSample() {
		ResponseEntity<AjaxResponse> responseEntity = controller.updateDefaultSequencingObjectForSample(SAMPLE.getId(), sequencingObjectIds.stream().findFirst().get(),
				Locale.ENGLISH);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "Response is ok");
	}

	@Test
	public void testGetSampleAnalyses(){
		ResponseEntity<List<SampleAnalyses>> responseEntity = controller.getSampleAnalyses(SAMPLE.getId(), principal, Locale.ENGLISH);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "Response is ok");
	}
}
