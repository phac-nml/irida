package ca.corefacility.bioinformatics.irida.ria.unit.web.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.repositories.sample.MetadataEntryRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.MetadataRestrictionRepository;
import ca.corefacility.bioinformatics.irida.ria.unit.TestDataFactory;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.MessageSource;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.samples.dto.SampleDetails;
import ca.corefacility.bioinformatics.irida.ria.web.samples.dto.ShareSamplesRequest;
import ca.corefacility.bioinformatics.irida.ria.web.services.UICartService;
import ca.corefacility.bioinformatics.irida.ria.web.services.UISampleService;
import ca.corefacility.bioinformatics.irida.security.permissions.sample.UpdateSamplePermission;
import ca.corefacility.bioinformatics.irida.service.GenomeAssemblyService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.collect.ImmutableList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class UISampleServiceTest {
	private UISampleService service;
	private ProjectService projectService;
	private SequencingObjectService sequencingObjectService;

	private SequencingObject sequencingObject;

	private final User USER_1 = new User("test", "test@nowhere.com", "PW1@3456", "Test", "Tester", "1234567890");
	private final Sample SAMPLE_1 = new Sample("SAMPLE_01");

	private final Long SAMPLE_ID = 313L;
	private final String SAMPLE_ORGANISM = "Salmonella";
	private final String SAMPLE_DESCRIPTION ="This is a project about interesting stuff";

	private final Sample ANOTHER_SAMPLE = TestDataFactory.constructSample();
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

	@Before
	public void setUp() {
		SampleService sampleService = mock(SampleService.class);
		sequencingObject = mock(SequencingObject.class);
		projectService = mock(ProjectService.class);
		UpdateSamplePermission updateSamplePermission = mock(UpdateSamplePermission.class);
		sequencingObjectService = mock(SequencingObjectService.class);
		GenomeAssemblyService genomeAssemblyService = mock(GenomeAssemblyService.class);
		MessageSource messageSource = mock(MessageSource.class);
		UICartService cartService = mock(UICartService.class);
		MetadataTemplateService metadataTemplateService = mock(MetadataTemplateService.class);
		MetadataEntryRepository metadataEntryRepository = mock(MetadataEntryRepository.class);
		MetadataRestrictionRepository metadataRestrictionRepository = mock(MetadataRestrictionRepository.class);
		service = new UISampleService(sampleService, projectService, updateSamplePermission, sequencingObjectService,
				genomeAssemblyService, messageSource, cartService, metadataTemplateService, metadataEntryRepository,
				metadataRestrictionRepository);

		// DATA
		SAMPLE_1.setId(SAMPLE_ID);
		SAMPLE_1.setDescription(SAMPLE_DESCRIPTION);
		SAMPLE_1.setOrganism(SAMPLE_ORGANISM);
		USER_1.setSystemRole(Role.ROLE_ADMIN);
		Authentication authentication = mock(Authentication.class);
		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);

		when(sampleService.read(1L)).thenReturn(SAMPLE_1);
		when(updateSamplePermission.isAllowed(authentication, SAMPLE_1)).thenReturn(true);

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
	public void testGetSampleDetails() {
		SampleDetails details = service.getSampleDetails(1L);
		final Sample sample = details.getSample();
		Assert.assertEquals("Should return the proper samples organism", SAMPLE_ORGANISM, sample.getOrganism());
		Assert.assertEquals("Should return the proper samples description", SAMPLE_DESCRIPTION, sample.getDescription());
		Assert.assertEquals("Should return the proper samples identifier", SAMPLE_ID, sample.getId());
	}

	@Test
	public void testShareSamplesWithProject() throws Exception {
		final Long CURRENT_PROJECT_ID = 1L;
		final Project CURRENT_PROJECT = new Project("CURRENT_PROJECT");
		CURRENT_PROJECT.setId(CURRENT_PROJECT_ID);
		when(projectService.read(CURRENT_PROJECT_ID)).thenReturn(CURRENT_PROJECT);

		final Long TARGET_PROJECT_ID = 2L;
		final Project TARGET_PROJECT = new Project("TARGET_PROJECT");
		TARGET_PROJECT.setId(TARGET_PROJECT_ID);
		when(projectService.read(TARGET_PROJECT_ID)).thenReturn(TARGET_PROJECT);

		ShareSamplesRequest request = new ShareSamplesRequest();
		request.setCurrentId(CURRENT_PROJECT_ID);
		request.setTargetId(TARGET_PROJECT_ID);
		request.setSampleIds(ImmutableList.of(SAMPLE_ID));
		request.setRemove(false);
		request.setLocked(false);
		service.shareSamplesWithProject(request, Locale.CANADA);
	}

	@Ignore
	@Test
	public void testUploadSequenceFiles() throws IOException {
		MultipartHttpServletRequest request = mock(MultipartHttpServletRequest.class);
		when(request.getFile(FILE_01)).thenReturn(MOCK_FILE_01);
		when(request.getFile(FILE_02)).thenReturn(MOCK_FILE_02);
		when(request.getFileNames()).thenReturn(SINGLE_FILE_NAMES.iterator());
				ArgumentCaptor<SingleEndSequenceFile> sequenceFileArgumentCaptor = ArgumentCaptor
						.forClass(SingleEndSequenceFile.class);

		service.uploadSequenceFiles(ANOTHER_SAMPLE.getId(), request);

		verify(sequencingObjectService, times(2)).createSequencingObjectInSample(sequenceFileArgumentCaptor.capture(),
				eq(ANOTHER_SAMPLE));
		assertEquals("Should have the correct file name", FILE_02, sequenceFileArgumentCaptor.getValue()
				.getLabel());
	}

	@Ignore
	@Test
	public void testUploadSequenceFilePairs() throws IOException {
		MultipartHttpServletRequest request = mock(MultipartHttpServletRequest.class);
		when(request.getFile(PAIR_01)).thenReturn(MOCK_PAIR_FILE_01);
		when(request.getFile(PAIR_02)).thenReturn(MOCK_PAIR_FILE_02);
		when(request.getFileNames()).thenReturn(PAIRED_FILE_NAMES.iterator());
		ArgumentCaptor<SequenceFilePair> sequenceFileArgumentCaptor = ArgumentCaptor.forClass(SequenceFilePair.class);
		service.uploadSequenceFiles(ANOTHER_SAMPLE.getId(), request);

		verify(sequencingObjectService)
				.createSequencingObjectInSample(sequenceFileArgumentCaptor.capture(), eq(ANOTHER_SAMPLE));

		assertEquals("Should have the correct file name", PAIR_01, sequenceFileArgumentCaptor
				.getValue().getForwardSequenceFile().getLabel());
		assertEquals("Should have the correct file name", PAIR_02, sequenceFileArgumentCaptor
				.getValue().getReverseSequenceFile().getLabel());
	}

	@Ignore
	@Test
	public void testUploadSequenceFilePairsAndSingle() throws IOException {
		MultipartHttpServletRequest request = mock(MultipartHttpServletRequest.class);
		when(request.getFile(FILE_01)).thenReturn(MOCK_FILE_01);
		when(request.getFile(PAIR_01)).thenReturn(MOCK_PAIR_FILE_01);
		when(request.getFile(PAIR_02)).thenReturn(MOCK_PAIR_FILE_02);
		when(request.getFileNames()).thenReturn(MIXED_FILE_NAMES.iterator());
		ArgumentCaptor<SequencingObject> sequenceFileArgumentCaptor = ArgumentCaptor.forClass(SequencingObject.class);
		service.uploadSequenceFiles(ANOTHER_SAMPLE.getId(), request);

		verify(sequencingObjectService, times(2)).createSequencingObjectInSample(sequenceFileArgumentCaptor.capture(),
				eq(ANOTHER_SAMPLE));

		List<SequencingObject> allValues = sequenceFileArgumentCaptor.getAllValues();

		assertEquals("Should have created 1 single end sequence files", 1,
				allValues.stream().filter(o -> o instanceof SingleEndSequenceFile).count());
		assertEquals("Should have created 1 file pair", 1, allValues.stream()
				.filter(o -> o instanceof SequenceFilePair).count());
	}
}
