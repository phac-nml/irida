package ca.corefacility.bioinformatics.irida.service.impl.unit.sample;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import ca.corefacility.bioinformatics.irida.exceptions.AnalysisAlreadySetException;
import ca.corefacility.bioinformatics.irida.exceptions.SequenceFileAnalysisException;
import ca.corefacility.bioinformatics.irida.model.assembly.UploadedAssembly;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleGenomeAssemblyJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.PipelineProvidedMetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.analysis.AnalysisRepository;
import ca.corefacility.bioinformatics.irida.repositories.assembly.GenomeAssemblyRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleGenomeAssemblyJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequencingObjectJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.MetadataEntryRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.QCEntryRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.SampleRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequencingObjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.service.GenomeAssemblyService;
import ca.corefacility.bioinformatics.irida.service.impl.GenomeAssemblyServiceImpl;
import ca.corefacility.bioinformatics.irida.service.impl.sample.SampleServiceImpl;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.validation.Validator;

/**
 * Unit tests for {@link SampleServiceImpl}.
 */
public class SampleServiceImplTest {

	private SampleService sampleService;
	private SampleRepository sampleRepository;
	private ProjectSampleJoinRepository psjRepository;
	private AnalysisRepository analysisRepository;
	private SampleSequencingObjectJoinRepository ssoRepository;
	private QCEntryRepository qcEntryRepository;
	private SequencingObjectRepository sequencingObjectRepository;
	private SampleGenomeAssemblyJoinRepository sampleGenomeAssemblyJoinRepository;
	private UserRepository userRepository;
	private MetadataEntryRepository metadataEntryRepository;

	private GenomeAssemblyRepository genomeAssemblyRepository;
	private GenomeAssemblyService genomeAssemblyService;

	private Validator validator;

	/**
	 * Variation in a floating point number to be considered equal.
	 */
	private static final double deltaFloatEquality = 0.000001;

	@BeforeEach
	public void setUp() {
		sampleRepository = mock(SampleRepository.class);
		psjRepository = mock(ProjectSampleJoinRepository.class);
		analysisRepository = mock(AnalysisRepository.class);
		ssoRepository = mock(SampleSequencingObjectJoinRepository.class);
		qcEntryRepository = mock(QCEntryRepository.class);
		sequencingObjectRepository = mock(SequencingObjectRepository.class);
		sampleGenomeAssemblyJoinRepository = mock(SampleGenomeAssemblyJoinRepository.class);
		metadataEntryRepository = mock(MetadataEntryRepository.class);


		sampleService = new SampleServiceImpl(sampleRepository, psjRepository, analysisRepository, ssoRepository,
				qcEntryRepository, sequencingObjectRepository, sampleGenomeAssemblyJoinRepository, userRepository,
				metadataEntryRepository, null);

		genomeAssemblyRepository = mock(GenomeAssemblyRepository.class);
		validator = mock(Validator.class);
		genomeAssemblyService = new GenomeAssemblyServiceImpl(genomeAssemblyRepository, sampleGenomeAssemblyJoinRepository, null, sampleService, sampleRepository);
	}

	@Test
	public void testGetSampleForProject() {
		Project p = new Project();
		p.setId(1111L);
		Sample s = new Sample();
		s.setId(2222L);

		ProjectSampleJoin join = new ProjectSampleJoin(p, s, true);

		when(sampleRepository.findById(s.getId())).thenReturn(Optional.of(s));
		when(psjRepository.readSampleForProject(p, s)).thenReturn(join);

		sampleService.getSampleForProject(p, s.getId());

		verify(psjRepository).readSampleForProject(p, s);
	}

	@Test
	public void testRemoveSequencingObjectFromSample() {
		Sample s = new Sample();
		s.setId(1111L);
		SequenceFile sf = new SequenceFile();
		sf.setId(2222L);
		SingleEndSequenceFile obj = new SingleEndSequenceFile(sf);
		obj.setId(2L);
		s.setDefaultSequencingObject(obj);
		SampleSequencingObjectJoin join = new SampleSequencingObjectJoin(s, obj);

		when(ssoRepository.readObjectForSample(s, obj.getId())).thenReturn(join);
		assertNotNull(s.getDefaultSequencingObject());
		assertEquals(s.getDefaultSequencingObject().getId(), 2L,
				"The default sequencing object id should match the sequencing object id");

		sampleService.removeSequencingObjectFromSample(s, obj);

		verify(ssoRepository).delete(join);
		verify(sampleRepository).removeDefaultSequencingObject(s);
	}

	@Test
	public void testRemoveGenomeAssemblyFromSample() {
		Sample s = new Sample();
		s.setId(1111L);
		UploadedAssembly uploadedAssembly = new UploadedAssembly(Paths.get("/path/to/file"));
		uploadedAssembly.setId(2L);
		s.setDefaultGenomeAssembly(uploadedAssembly);
		SampleGenomeAssemblyJoin join = new SampleGenomeAssemblyJoin(s, uploadedAssembly);

		when(sampleGenomeAssemblyJoinRepository.findBySampleAndAssemblyId(s.getId(),
				uploadedAssembly.getId())).thenReturn(join);
		assertNotNull(s.getDefaultGenomeAssembly());
		assertEquals(s.getDefaultGenomeAssembly().getId(), 2L,
				"The default genome assembly should match the genome assembly id");

		genomeAssemblyService.removeGenomeAssemblyFromSample(s, uploadedAssembly.getId());

		verify(sampleGenomeAssemblyJoinRepository).delete(join);
		verify(sampleRepository).removeDefaultGenomeAssembly(s);
	}

	@Test
	public void testMergeSamples() {
		// For every sample in toMerge, the service should:
		// 1. call SequenceFileRepository to get the sequence files in that
		// sample,
		// 2. call SequenceFileRepository to add the sequence files to
		// mergeInto,
		// 3. call SampleRepository to persist the sample as deleted.

		final int SIZE = 3;

		Sample s = s(1L);
		Project project = p(1L);

		Sample[] toMerge = new Sample[SIZE];
		SequenceFile[] toMerge_sf = new SequenceFile[SIZE];
		SequencingObject[] toMerge_so = new SequencingObject[SIZE];
		SampleSequencingObjectJoin[] s_so_joins = new SampleSequencingObjectJoin[SIZE];
		SampleSequencingObjectJoin[] s_so_original = new SampleSequencingObjectJoin[SIZE];
		ProjectSampleJoin[] p_s_joins = new ProjectSampleJoin[SIZE];

		List<Sample> mergeSamples = new ArrayList<>();

		for (long i = 0; i < SIZE; i++) {
			int p = (int) i;
			toMerge[p] = s(i + 2);
			mergeSamples.add(toMerge[p]);
			toMerge_sf[p] = sf(i + 2);
			toMerge_so[p] = so(i + 2);
			s_so_joins[p] = new SampleSequencingObjectJoin(s, toMerge_so[p]);
			p_s_joins[p] = new ProjectSampleJoin(project, toMerge[p], true);

			List<Join<Project, Sample>> projectSampleJoins = new ArrayList<>();
			projectSampleJoins.add(p_s_joins[p]);

			List<SampleSequencingObjectJoin> sampleSeqObjectJoins = new ArrayList<>();

			SampleSequencingObjectJoin join = new SampleSequencingObjectJoin(toMerge[p], toMerge_so[p]);
			sampleSeqObjectJoins.add(join);

			s_so_original[p] = join;

			when(ssoRepository.getSequencesForSample(toMerge[p])).thenReturn(null);
			when(ssoRepository.getSequencesForSample(toMerge[p])).thenReturn(sampleSeqObjectJoins);
			when(ssoRepository.save(s_so_joins[p])).thenReturn(s_so_joins[p]);
			when(ssoRepository.readObjectForSample(toMerge[p], toMerge_so[p].getId())).thenReturn(join);
			when(psjRepository.getProjectForSample(toMerge[p])).thenReturn(projectSampleJoins);

			// for deletion
			when(psjRepository.readSampleForProject(project, toMerge[p])).thenReturn(p_s_joins[p]);
		}
		List<Join<Project, Sample>> joins = new ArrayList<>();
		joins.add(new ProjectSampleJoin(project, s, true));
		when(psjRepository.getProjectForSample(s)).thenReturn(joins);

		Sample saved = sampleService.mergeSamples(project, s, mergeSamples);

		verify(psjRepository).getProjectForSample(s);
		for (int i = 0; i < SIZE; i++) {
			verify(ssoRepository).getSequencesForSample(toMerge[i]);
			verify(ssoRepository).save(s_so_joins[i]);
			verify(ssoRepository).delete(s_so_original[i]);
			verify(sampleRepository).deleteById(toMerge[i].getId());
			verify(psjRepository).getProjectForSample(toMerge[i]);
			verify(psjRepository).delete(p_s_joins[i]);
		}
		assertEquals(s, saved, "The saved sample should be the same as the sample to merge into.");
	}

	@Test
	public void testRejectSampleMergeDifferentProjects() {
		Sample s1 = new Sample();
		s1.setId(1L);
		Sample s2 = new Sample();
		s2.setId(2L);
		Project p1 = new Project();
		p1.setId(1L);
		p1.setName("project 1");
		Project p2 = new Project();
		p2.setId(2L);
		p2.setName("project 2");

		List<Join<Project, Sample>> p1_s1 = new ArrayList<>();
		p1_s1.add(new ProjectSampleJoin(p1, s1, true));
		List<Join<Project, Sample>> p2_s2 = new ArrayList<>();
		p2_s2.add(new ProjectSampleJoin(p2, s2, true));

		when(psjRepository.getProjectForSample(s1)).thenReturn(p1_s1);
		when(psjRepository.getProjectForSample(s2)).thenReturn(p2_s2);

		try {
			sampleService.mergeSamples(p1, s1, Lists.newArrayList(s2));
			fail("Samples from different projects were allowed to be merged.");
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			e.printStackTrace();
			fail("Failed for an unknown reason; stack trace preceded.");
		}

		verify(psjRepository).getProjectForSample(s1);
		verify(psjRepository).getProjectForSample(s2);
	}

	/**
	 * Tests out successfully getting the coverage from a sample with no sequence files.
	 *
	 * @throws SequenceFileAnalysisException
	 */
	@Test
	public void testGetCoverageForSampleSuccessZero() throws SequenceFileAnalysisException {
		Sample s1 = new Sample();
		s1.setId(1L);

		when(ssoRepository.getSequencesForSample(s1)).thenReturn(Lists.newArrayList());

		double coverage = sampleService.estimateCoverageForSample(s1, 10);
		assertEquals(0, coverage, deltaFloatEquality);
	}

	/**
	 * Tests out successfully getting the coverage from a sample with a sequence file.
	 *
	 * @throws SequenceFileAnalysisException
	 * @throws AnalysisAlreadySetException
	 */
	@Test
	public void testGetCoverageForSampleSuccess() throws SequenceFileAnalysisException, AnalysisAlreadySetException {
		Sample s1 = new Sample();
		s1.setId(1L);

		SequenceFile sf1 = new SequenceFile();
		sf1.setId(2222L);

		SampleSequencingObjectJoin join = new SampleSequencingObjectJoin(s1, new SingleEndSequenceFile(sf1));

		AnalysisFastQC analysisFastQC1 = AnalysisFastQC.builder()
				.executionManagerAnalysisId("id")
				.totalBases(1000L)
				.build();
		sf1.setFastQCAnalysis(analysisFastQC1);

		when(ssoRepository.getSequencesForSample(s1)).thenReturn(Arrays.asList(join));
		when(analysisRepository.findFastqcAnalysisForSequenceFile(sf1)).thenReturn(analysisFastQC1);

		double coverage = sampleService.estimateCoverageForSample(s1, 500L);
		assertEquals(coverage, deltaFloatEquality, 2.0);
	}

	/**
	 * Tests out passing an invalid reference length.
	 *
	 * @throws SequenceFileAnalysisException
	 */
	@Test
	public void testGetCoverageForSampleInvalidReferenceLength() throws SequenceFileAnalysisException {
		assertThrows(IllegalArgumentException.class, () -> {
			sampleService.estimateCoverageForSample(new Sample(), 0L);
		});
	}

	/**
	 * Tests out successfully getting the total bases from a sample with no sequence files.
	 *
	 * @throws SequenceFileAnalysisException
	 */
	@Test
	public void testGetTotalBasesForSampleSuccessZero() throws SequenceFileAnalysisException {
		Sample s1 = new Sample();
		s1.setId(1L);

		when(ssoRepository.getSequencesForSample(s1)).thenReturn(Lists.newArrayList());

		long actualBases = sampleService.getTotalBasesForSample(s1);
		assertEquals(0, actualBases);
	}

	/**
	 * Tests out successfully getting the total bases from a sample with one sequence file.
	 *
	 * @throws SequenceFileAnalysisException
	 * @throws AnalysisAlreadySetException
	 */
	@Test
	public void testGetTotalBasesForSampleSuccessOne()
			throws SequenceFileAnalysisException, AnalysisAlreadySetException {
		Sample s1 = new Sample();
		s1.setId(1L);

		SequenceFile sf1 = new SequenceFile();
		sf1.setId(2222L);

		SampleSequencingObjectJoin join = new SampleSequencingObjectJoin(s1, new SingleEndSequenceFile(sf1));

		AnalysisFastQC analysisFastQC1 = AnalysisFastQC.builder()
				.executionManagerAnalysisId("id")
				.totalBases(1000L)
				.build();
		sf1.setFastQCAnalysis(analysisFastQC1);

		when(ssoRepository.getSequencesForSample(s1)).thenReturn(Arrays.asList(join));
		when(analysisRepository.findFastqcAnalysisForSequenceFile(sf1)).thenReturn(analysisFastQC1);

		long actualBases = sampleService.getTotalBasesForSample(s1);
		assertEquals(1000, actualBases);
	}

	/**
	 * Tests out successfully getting the total bases from a sample with two sequence files.
	 *
	 * @throws SequenceFileAnalysisException
	 * @throws AnalysisAlreadySetException
	 */
	@Test
	public void testGetTotalBasesForSampleSuccessTwo()
			throws SequenceFileAnalysisException, AnalysisAlreadySetException {
		Sample s1 = new Sample();
		s1.setId(1L);

		SequenceFile sf1 = new SequenceFile();
		sf1.setId(2222L);
		SequenceFile sf2 = new SequenceFile();
		sf1.setId(3333L);

		SampleSequencingObjectJoin join1 = new SampleSequencingObjectJoin(s1, new SingleEndSequenceFile(sf1));
		SampleSequencingObjectJoin join2 = new SampleSequencingObjectJoin(s1, new SingleEndSequenceFile(sf2));

		AnalysisFastQC analysisFastQC1 = AnalysisFastQC.builder()
				.executionManagerAnalysisId("id")
				.totalBases(1000L)
				.build();
		sf1.setFastQCAnalysis(analysisFastQC1);

		AnalysisFastQC analysisFastQC2 = AnalysisFastQC.builder()
				.executionManagerAnalysisId("id2")
				.totalBases(1000L)
				.build();
		sf2.setFastQCAnalysis(analysisFastQC2);

		when(ssoRepository.getSequencesForSample(s1)).thenReturn(Arrays.asList(join1, join2));
		when(analysisRepository.findFastqcAnalysisForSequenceFile(sf1)).thenReturn(analysisFastQC1);
		when(analysisRepository.findFastqcAnalysisForSequenceFile(sf2)).thenReturn(analysisFastQC2);

		long actualBases = sampleService.getTotalBasesForSample(s1);
		assertEquals(2000, actualBases);
	}

	/**
	 * Tests out failing to get the total bases from a sample with one sequence file due to missing FastQC
	 *
	 * @throws SequenceFileAnalysisException
	 */
	@Test
	public void testGetTotalBasesForSampleFailNoFastQC() throws SequenceFileAnalysisException {
		Sample s1 = new Sample();
		s1.setId(1L);

		SequenceFile sf1 = new SequenceFile();
		sf1.setId(2222L);

		SampleSequencingObjectJoin join = new SampleSequencingObjectJoin(s1, new SingleEndSequenceFile(sf1));

		when(ssoRepository.getSequencesForSample(s1)).thenReturn(Arrays.asList(join));

		assertThrows(SequenceFileAnalysisException.class, () -> {
			sampleService.getTotalBasesForSample(s1);
		});
	}

	/**
	 * Tests out failing to get the total bases from a sample with one sequence file due to too many FastQC
	 *
	 * @throws SequenceFileAnalysisException
	 */
	@Test
	public void testGetTotalBasesForSampleFailMultipleFastQC() throws SequenceFileAnalysisException {
		Sample s1 = new Sample();
		s1.setId(1L);

		SequenceFile sf1 = new SequenceFile();
		sf1.setId(2222L);

		SampleSequencingObjectJoin join = new SampleSequencingObjectJoin(s1, new SingleEndSequenceFile(sf1));

		when(ssoRepository.getSequencesForSample(s1)).thenReturn(Arrays.asList(join));

		assertThrows(SequenceFileAnalysisException.class, () -> {
			sampleService.getTotalBasesForSample(s1);
		});
	}

	@Test
	public void testMergeMetadataSuccess() {
		Sample s1 = new Sample();
		s1.setId(1L);

		Date originalModifiedDate = s1.getModifiedDate();

		when(sampleRepository.findById(s1.getId())).thenReturn(Optional.of(s1));

		MetadataTemplateField field1 = new MetadataTemplateField("field1", "text");
		MetadataEntry entry1 = new MetadataEntry("entry1", "text", field1);
		MetadataTemplateField field2 = new MetadataTemplateField("field2", "text");
		MetadataEntry entry2 = new MetadataEntry("entry2", "text", field2);
		Set<MetadataEntry> metadataEntries = Sets.newHashSet(entry1, entry2);

		when(metadataEntryRepository.getMetadataForSample(s1)).thenReturn(metadataEntries);

		MetadataEntry entry = new MetadataEntry("entry2", "text", field1);

		Set<MetadataEntry> inputMetadata = Sets.newHashSet(entry);

		sampleService.mergeSampleMetadata(s1, inputMetadata);

		@SuppressWarnings("unchecked")
		ArgumentCaptor<Set<MetadataEntry>> saveCaptor = ArgumentCaptor.forClass(Set.class);

		verify(metadataEntryRepository).saveAll(saveCaptor.capture());

		// ensure modified date got bumped up
		ArgumentCaptor<Sample> sampleCaptor = ArgumentCaptor.forClass(Sample.class);
		verify(sampleRepository).save(sampleCaptor.capture());

		Date savedModifiedDate = sampleCaptor.getValue().getModifiedDate();

		assertNotEquals(originalModifiedDate, savedModifiedDate);

		Set<MetadataEntry> savedValues = saveCaptor.getValue();

		assertEquals(2, savedValues.size(), "should be 2 entries");

		Optional<MetadataEntry> optValue = savedValues.stream().filter(e -> e.getField().equals(field1)).findAny();

		assertTrue(optValue.isPresent(), "should find a value with the given field");

		MetadataEntry metadataEntry = optValue.get();

		assertEquals(entry.getValue(), metadataEntry.getValue(), "value should have been updated");
	}

	@Test
	public void testMergeMetadataDifferentTypes() {
		Sample s1 = new Sample();
		s1.setId(1L);

		when(sampleRepository.findById(s1.getId())).thenReturn(Optional.of(s1));

		MetadataTemplateField field1 = new MetadataTemplateField("field1", "text");
		AnalysisSubmission submission = AnalysisSubmission.builder(UUID.randomUUID())
				.inputFiles(Sets.newHashSet(new SequenceFilePair()))
				.build();
		submission.setId(2L);

		PipelineProvidedMetadataEntry entry1 = new PipelineProvidedMetadataEntry("entry2", "text", submission);
		entry1.setField(field1);

		MetadataTemplateField field2 = new MetadataTemplateField("field2", "text");
		MetadataEntry entry2 = new MetadataEntry("entry2", "text", field2);
		Set<MetadataEntry> metadataEntries = Sets.newHashSet(entry1, entry2);

		when(metadataEntryRepository.getMetadataForSample(s1)).thenReturn(metadataEntries);

		MetadataEntry entry = new MetadataEntry("entry2", "text", field1);

		Set<MetadataEntry> inputMetadata = Sets.newHashSet(entry);

		sampleService.mergeSampleMetadata(s1, inputMetadata);

		@SuppressWarnings("unchecked")
		ArgumentCaptor<Set<MetadataEntry>> saveCaptor = ArgumentCaptor.forClass(Set.class);

		verify(metadataEntryRepository).saveAll(saveCaptor.capture());

		Set<MetadataEntry> savedValues = saveCaptor.getValue();

		assertEquals(2, savedValues.size(), "should be 2 entries");

		Optional<MetadataEntry> optValue = savedValues.stream().filter(e -> e.getField().equals(field1)).findAny();

		assertTrue(optValue.isPresent(), "should find a value with the given field");

		MetadataEntry metadataEntry = optValue.get();

		assertEquals(entry.getValue(), metadataEntry.getValue(), "value should have been updated");

		Optional<MetadataEntry> pipelineOpt = savedValues.stream()
				.filter(e -> e instanceof PipelineProvidedMetadataEntry)
				.findAny();

		assertTrue(pipelineOpt.isEmpty(), "should be no pipeline entries left");
	}

	@Test
	public void testUpdateSampleMetadata() {
		Sample s1 = new Sample();
		s1.setId(1L);

		Date originalModifiedDate = s1.getModifiedDate();

		when(sampleRepository.findById(s1.getId())).thenReturn(Optional.of(s1));

		MetadataTemplateField field1 = new MetadataTemplateField("field1", "text");
		MetadataEntry entry1 = new MetadataEntry("entry1", "text", field1);
		MetadataTemplateField field2 = new MetadataTemplateField("field2", "text");
		MetadataEntry entry2 = new MetadataEntry("entry2", "text", field2);
		Set<MetadataEntry> metadataEntries = Sets.newHashSet(entry1, entry2);

		when(metadataEntryRepository.getMetadataForSample(s1)).thenReturn(metadataEntries);

		MetadataEntry entry = new MetadataEntry("entry2", "text", field1);

		Set<MetadataEntry> inputMetadata = Sets.newHashSet(entry);

		sampleService.updateSampleMetadata(s1, inputMetadata);

		verify(metadataEntryRepository).deleteAll(metadataEntries);

		@SuppressWarnings("unchecked")
		ArgumentCaptor<Set<MetadataEntry>> saveCaptor = ArgumentCaptor.forClass(Set.class);

		verify(metadataEntryRepository).saveAll(saveCaptor.capture());

		// ensure modified date got bumped up
		ArgumentCaptor<Sample> sampleCaptor = ArgumentCaptor.forClass(Sample.class);
		verify(sampleRepository).save(sampleCaptor.capture());

		Date savedModifiedDate = sampleCaptor.getValue().getModifiedDate();

		assertNotEquals(originalModifiedDate, savedModifiedDate);

		Set<MetadataEntry> savedValues = saveCaptor.getValue();

		assertEquals(1, savedValues.size());

		MetadataEntry savedEntry = savedValues.iterator().next();

		assertEquals(entry.getValue(), savedEntry.getValue());
		assertEquals(entry.getField(), savedEntry.getField());
	}

	private Sample s(Long id) {
		Sample s = new Sample();
		s.setId(id);
		return s;
	}

	private SequenceFile sf(Long id) {
		SequenceFile sf = new SequenceFile();
		sf.setId(id);
		try {
			sf.setFile(Files.createTempFile(null, null));
		} catch (IOException e) {

		}
		return sf;
	}

	private SequencingObject so(Long id) {
		SequencingObject so = new SingleEndSequenceFile(sf(id));
		so.setId(id);
		return so;
	}

	private Project p(Long id) {
		Project p = new Project();
		p.setId(id);
		return p;
	}
}
