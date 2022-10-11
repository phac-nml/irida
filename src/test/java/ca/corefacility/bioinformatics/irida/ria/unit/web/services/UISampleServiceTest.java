package ca.corefacility.bioinformatics.irida.ria.unit.web.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Locale;

import ca.corefacility.bioinformatics.irida.exceptions.ConcatenateException;
import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleGenomeAssemblyJoin;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.*;
import ca.corefacility.bioinformatics.irida.repositories.sample.MetadataEntryRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.MetadataRestrictionRepository;
import ca.corefacility.bioinformatics.irida.ria.unit.TestDataFactory;
import ca.corefacility.bioinformatics.irida.ria.web.samples.dto.*;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;

import ca.corefacility.bioinformatics.irida.repositories.specification.ProjectSampleJoinSpecification;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.AntPagination;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.AntSort;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.AntTableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectSampleTableItem;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectSamplesTableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.samples.dto.SampleDetails;
import ca.corefacility.bioinformatics.irida.ria.web.samples.dto.ShareMetadataRestriction;
import ca.corefacility.bioinformatics.irida.ria.web.samples.dto.ShareSamplesRequest;

import ca.corefacility.bioinformatics.irida.ria.web.services.UICartService;
import ca.corefacility.bioinformatics.irida.ria.web.services.UISampleService;
import ca.corefacility.bioinformatics.irida.security.permissions.sample.UpdateSamplePermission;
import ca.corefacility.bioinformatics.irida.service.GenomeAssemblyService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class UISampleServiceTest {
	private UISampleService service;
	private ProjectService projectService;
	private SequencingObjectService sequencingObjectService;
	private GenomeAssemblyService genomeAssemblyService;
	private SampleService sampleService;

	private SequencingObject sequencingObject;
	private GenomeAssembly genomeAssembly;

	private final User USER_1 = new User("test", "test@nowhere.com", "PW1@3456", "Test", "Tester", "1234567890");
	private final Sample SAMPLE_1 = new Sample("SAMPLE_01");

	private final Long SAMPLE_ID = 313L;
	private final String SAMPLE_ORGANISM = "Salmonella";
	private final String SAMPLE_DESCRIPTION = "This is a project about interesting stuff";

	private final Sample ANOTHER_SAMPLE = TestDataFactory.constructSample();
	private final String FILE_01 = "test_file_A.fastq";
	private final String FILE_02 = "test_file_B.fastq";
	private final String PAIR_01 = "pair_test_R1_001.fastq";
	private final String PAIR_02 = "pair_test_R2_001.fastq";
	private final String FAST5_01 = "testfast5file.fast5";
	private final String ASSEMBLY_01 = "test_file.fasta";
	private final List<String> SINGLE_FILE_NAMES = ImmutableList.of(FILE_01, FILE_02);
	private final List<String> PAIRED_FILE_NAMES = ImmutableList.of(PAIR_01, PAIR_02);
	private final List<String> FAST5_FILE_NAMES = ImmutableList.of(FAST5_01);
	private final List<String> ASSEMBLY_FILE_NAMES = ImmutableList.of(ASSEMBLY_01);
	private final List<String> MIXED_FILE_NAMES = ImmutableList.of(FILE_01, FILE_02, PAIR_01, PAIR_02);
	MockMultipartFile MOCK_FILE_01;
	MockMultipartFile MOCK_FILE_02;
	MockMultipartFile MOCK_PAIR_FILE_01;
	MockMultipartFile MOCK_PAIR_FILE_02;
	MockMultipartFile MOCK_FAST5_FILE_01;
	MockMultipartFile MOCK_ASSEMBLY_FILE_01;

	SampleSequencingObjectJoin sampleSequencingObjectJoin;
	SampleGenomeAssemblyJoin sampleGenomeAssemblyJoin;

	Sample sample2 = new Sample("SAMPLE_02");
	Sample sample3 = new Sample("SAMPLE_03");
	private final Long PROJECT_ID_1 = 1L;
	private final Long PROJECT_ID_2 = 2L;
	private Project PROJECT_1 = new Project("PROJECT 1");
	private Project PROJECT_2 = new Project("PROJECT 2");
	private final ProjectSamplesTableRequest request = new ProjectSamplesTableRequest();
	private final ProjectSampleJoinSpecification specification = new ProjectSampleJoinSpecification();
	List<ProjectSampleJoin> joins = ImmutableList.of(new ProjectSampleJoin(PROJECT_1, SAMPLE_1, true),
			new ProjectSampleJoin(PROJECT_2, sample2, true), new ProjectSampleJoin(PROJECT_2, sample3, true));


	@BeforeEach
	public void setUp() {
		sampleService = mock(SampleService.class);
		sequencingObject = mock(SequencingObject.class);
		genomeAssembly = mock(GenomeAssembly.class);
		projectService = mock(ProjectService.class);
		UpdateSamplePermission updateSamplePermission = mock(UpdateSamplePermission.class);

		sequencingObjectService = mock(SequencingObjectService.class);
		sampleSequencingObjectJoin = mock(SampleSequencingObjectJoin.class);
		sampleGenomeAssemblyJoin = mock(SampleGenomeAssemblyJoin.class);
		genomeAssemblyService = mock(GenomeAssemblyService.class);

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
		PROJECT_1.setId(PROJECT_ID_1);
		PROJECT_2.setId(PROJECT_ID_2);
		AntPagination pagination = new AntPagination(0, 10);
		request.setPagination(pagination);
		request.setSearch(ImmutableList.of());
		request.setOrder(ImmutableList.of(new AntSort("modifiedDate", "desc")));

		Authentication authentication = mock(Authentication.class);
		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		Page<ProjectSampleJoin> page = new PageImpl<>(joins);

		when(sampleService.read(1L)).thenReturn(SAMPLE_1);
		when(updateSamplePermission.isAllowed(authentication, SAMPLE_1)).thenReturn(true);

		MOCK_FILE_01 = createMultiPartFile(FILE_01, "src/test/resources/files/test_file_A.fastq");
		MOCK_FILE_02 = createMultiPartFile(FILE_02, "src/test/resources/files/test_file_B.fastq");
		MOCK_PAIR_FILE_01 = createMultiPartFile(PAIR_01, "src/test/resources/files/pairs/pair_test_R1_001.fastq");
		MOCK_PAIR_FILE_02 = createMultiPartFile(PAIR_02, "src/test/resources/files/pairs/pair_test_R2_001.fastq");
		MOCK_FAST5_FILE_01 = createMultiPartFile(FAST5_01, "src/test/resources/files/testfast5file.fast5");
		MOCK_ASSEMBLY_FILE_01 = createMultiPartFile(ASSEMBLY_01, "src/test/resources/files/test_file.fasta");

		when(sampleService.getFilteredProjectSamples(anyList(), any(ProjectSampleJoinSpecification.class), anyInt(),
				anyInt(), any(Sort.class))).thenReturn(page);
	}

	private MockMultipartFile createMultiPartFile(String name, String path) {
		try {
			FileInputStream fis = new FileInputStream(path);
			String contents = IOUtils.toString(fis, "UTF-8");
			return new MockMultipartFile(name, name, "text/plain", contents.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Test
	public void testGetSampleDetails() {
		Project project = new Project("newProject");
		project.setId(1L);
		when(projectService.read(anyLong())).thenReturn(project);

		SampleDetails details = service.getSampleDetails(1L, project.getId());
		final Sample sample = details.getSample();
		assertEquals(SAMPLE_ORGANISM, sample.getOrganism(), "Should return the proper samples organism");
		assertEquals(SAMPLE_DESCRIPTION, sample.getDescription(), "Should return the proper samples description");
		assertEquals(SAMPLE_ID, sample.getId(), "Should return the proper samples identifier");
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
		request.setRestrictions(ImmutableList.of(new ShareMetadataRestriction(1L, "LEVEL_1")));
		request.setRemove(false);
		request.setLocked(false);
		service.shareSamplesWithProject(request, Locale.CANADA);
	}

	@Test
	public void testUploadSingleEndSequenceFiles() throws IOException {
		MultipartHttpServletRequest request = mock(MultipartHttpServletRequest.class);
		when(request.getFile(FILE_01)).thenReturn(MOCK_FILE_01);
		when(request.getFile(FILE_02)).thenReturn(MOCK_FILE_02);
		when(request.getFileNames()).thenReturn(SINGLE_FILE_NAMES.iterator());

		List<String> fileNamesList = new ArrayList<>();
		fileNamesList.add(FILE_01);
		fileNamesList.add(FILE_02);

		when(sequencingObjectService.createSequencingObjectInSample(any(SingleEndSequenceFile.class),
				any(Sample.class))).thenReturn(sampleSequencingObjectJoin);

		when(sampleSequencingObjectJoin.getObject()).thenReturn(sequencingObject);
		List<SampleSequencingObjectFileModel> sampleSequencingObjectFileModels = service.uploadSequenceFiles(
				ANOTHER_SAMPLE.getId(), request);

		assertEquals(2, sampleSequencingObjectFileModels.size(), "Should have uploaded 2 single end sequence files");
	}

	@Test
	public void testUploadSequenceFilePairs() throws IOException {
		MultipartHttpServletRequest request = mock(MultipartHttpServletRequest.class);
		when(request.getFile(PAIR_01)).thenReturn(MOCK_PAIR_FILE_01);
		when(request.getFile(PAIR_02)).thenReturn(MOCK_PAIR_FILE_02);
		when(request.getFileNames()).thenReturn(PAIRED_FILE_NAMES.iterator());

		List<String> fileNamesList = new ArrayList<>();
		fileNamesList.add(PAIR_01);
		fileNamesList.add(PAIR_02);

		when(sequencingObjectService.createSequencingObjectInSample(any(SequenceFilePair.class),
				any(Sample.class))).thenReturn(sampleSequencingObjectJoin);

		when(sampleSequencingObjectJoin.getObject()).thenReturn(sequencingObject);
		List<SampleSequencingObjectFileModel> sampleSequencingObjectFileModels = service.uploadSequenceFiles(
				ANOTHER_SAMPLE.getId(), request);

		assertEquals(1, sampleSequencingObjectFileModels.size(), "Should have uploaded 1 sequence file pair");
	}

	@Test
	public void testUploadFast5() throws IOException {
		MultipartHttpServletRequest request = mock(MultipartHttpServletRequest.class);
		when(request.getFile(FAST5_01)).thenReturn(MOCK_FAST5_FILE_01);
		when(request.getFileNames()).thenReturn(FAST5_FILE_NAMES.iterator());

		List<String> fileNamesList = new ArrayList<>();
		fileNamesList.add(FAST5_01);

		when(sequencingObjectService.createSequencingObjectInSample(any(Fast5Object.class),
				any(Sample.class))).thenReturn(sampleSequencingObjectJoin);

		when(sampleSequencingObjectJoin.getObject()).thenReturn(sequencingObject);
		List<SampleSequencingObjectFileModel> sampleSequencingObjectFileModels = service.uploadFast5Files(
				ANOTHER_SAMPLE.getId(), request);

		assertEquals(1, sampleSequencingObjectFileModels.size(), "Should have uploaded 1 fast5 files");
	}

	@Test
	public void testUploadAssemblies() throws IOException {
		MultipartHttpServletRequest request = mock(MultipartHttpServletRequest.class);
		when(request.getFile(ASSEMBLY_01)).thenReturn(MOCK_ASSEMBLY_FILE_01);
		when(request.getFileNames()).thenReturn(ASSEMBLY_FILE_NAMES.iterator());

		List<String> fileNamesList = new ArrayList<>();
		fileNamesList.add(ASSEMBLY_01);

		when(genomeAssemblyService.createAssemblyInSample(any(Sample.class), any(GenomeAssembly.class))).thenReturn(
				sampleGenomeAssemblyJoin);

		when(sampleGenomeAssemblyJoin.getObject()).thenReturn(genomeAssembly);
		List<SampleGenomeAssemblyFileModel> sampleGenomeAssemblyFileModels = service.uploadAssemblies(
				ANOTHER_SAMPLE.getId(), request);

		assertEquals(1, sampleGenomeAssemblyFileModels.size(), "Should have uploaded 1 assembly file");
	}

	@Test
	public void testUploadSingleAndPairedEndFiles() throws IOException {
		MultipartHttpServletRequest request = mock(MultipartHttpServletRequest.class);
		when(request.getFile(FILE_01)).thenReturn(MOCK_FILE_01);
		when(request.getFile(FILE_02)).thenReturn(MOCK_FILE_02);
		when(request.getFile(PAIR_01)).thenReturn(MOCK_PAIR_FILE_01);
		when(request.getFile(PAIR_02)).thenReturn(MOCK_PAIR_FILE_02);
		when(request.getFileNames()).thenReturn(MIXED_FILE_NAMES.iterator());

		List<String> fileNamesList = new ArrayList<>();
		fileNamesList.add(FILE_01);
		fileNamesList.add(FILE_02);
		fileNamesList.add(PAIR_01);
		fileNamesList.add(PAIR_02);

		when(sequencingObjectService.createSequencingObjectInSample(any(SingleEndSequenceFile.class),
				any(Sample.class))).thenReturn(sampleSequencingObjectJoin);

		when(sequencingObjectService.createSequencingObjectInSample(any(SequenceFilePair.class),
				any(Sample.class))).thenReturn(sampleSequencingObjectJoin);

		when(sampleSequencingObjectJoin.getObject()).thenReturn(sequencingObject);

		List<SampleSequencingObjectFileModel> sampleSequencingObjectFileModels = service.uploadSequenceFiles(
				ANOTHER_SAMPLE.getId(), request);

		assertEquals(3, sampleSequencingObjectFileModels.size(),
				"Should have uploaded 2 single end sequence files and 1  sequence file pair");
	}

	@Test
	public void testConcatenateFiles() throws ConcatenateException {
		List<SampleSequencingObjectJoin> sequencingObjectJoins = TestDataFactory.generateSingleFileSequencingObjectsForSample(
				SAMPLE_1, Lists.newArrayList(FILE_01, FILE_02));

		SampleSequencingObjectJoin expectedConcatenatedFile = TestDataFactory.generateSingleFileSequencingObjectsForSample(
				SAMPLE_1, Lists.newArrayList("test_file_AB.fastq"))
				.get(0);

		List<SequencingObject> sequencingObjectList = sequencingObjectJoins.stream()
				.map(s -> s.getObject())
				.collect(Collectors.toList());
		Set<Long> sequencingObjectsIdList = sequencingObjectList.stream()
				.map(s -> s.getId())
				.collect(Collectors.toSet());

		when(sampleService.read(SAMPLE_ID)).thenReturn(SAMPLE_1);
		when(sequencingObjectService.readMultiple(sequencingObjectsIdList)).thenReturn(sequencingObjectList);
		when(sequencingObjectService.concatenateSequences(eq(Lists.newArrayList(sequencingObjectList)),
				eq("test_file_AB"), eq(SAMPLE_1), eq(false))).thenReturn(
				new SampleSequencingObjectJoin(SAMPLE_1, expectedConcatenatedFile.getObject()));
		when(sampleSequencingObjectJoin.getObject()).thenReturn(expectedConcatenatedFile.getObject());

		SampleConcatenationModel sampleConcatenationModel = service.concatenateSequenceFiles(
				SAMPLE_1.getId(), sequencingObjectsIdList, "test_file_AB", false, Locale.ENGLISH);

		assertEquals(1, sampleConcatenationModel.getSampleSequencingObjectFileModels().size(), "Should have concatenated 2 single end files into 1");

		assertEquals(expectedConcatenatedFile.getObject()
						.getFiles()
						.stream()
						.findFirst()
						.get()
						.getLabel(), sampleConcatenationModel.getSampleSequencingObjectFileModels().get(0)
						.getFileInfo()
						.getLabel(),
				"The concatenated file name should be the same as the SampleSequencingObject -> SequencingObject -> File name");
	}

	@Test
	public void testUpdateDefaultSequencingObjectForSample() {
		when(sampleService.read(SAMPLE_ID)).thenReturn(SAMPLE_1);
		when(sequencingObjectService.readSequencingObjectForSample(SAMPLE_1, sequencingObject.getId())).thenReturn(
				sequencingObject);
		service.updateDefaultSequencingObjectForSample(SAMPLE_ID, sequencingObject.getId(), Locale.ENGLISH);
		verify(sampleService, times(1)).read(SAMPLE_ID);
		verify(sequencingObjectService, times(1)).readSequencingObjectForSample(SAMPLE_1, sequencingObject.getId());
		verify(sampleService, times(1)).update(SAMPLE_1);
		assertEquals(sequencingObject, SAMPLE_1.getDefaultSequencingObject(),
				"Sequencing object should be set as default for sample");
	}

	public void testGetPagedProjectSamples() {
		AntTableResponse<ProjectSampleTableItem> response = service.getPagedProjectSamples(1L, request, Locale.CANADA);
		assertEquals(3, response.getTotal(), "Should return 3 items");
		List<?> items = response.getContent();
		for (int index = 0; index < items.size(); index++) {
			assertTrue(items.get(index) instanceof ProjectSampleTableItem, "Should return ProjectSampleTableItems");
			assertEquals(((ProjectSampleTableItem) items.get(index)).getProject().getName(),
					joins.get(index).getSubject().getName(), "Should return the proper project name");

		}
	}
}
