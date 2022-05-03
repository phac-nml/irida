package ca.corefacility.bioinformatics.irida.service.impl.integration.sample;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.annotation.ServiceIntegrationTest;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.SequenceFileAnalysisException;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.QCEntry;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.ProjectMetadataResponse;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleGenomeAssemblyJoinRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

/**
 * Integration tests for the sample service.
 */
@ServiceIntegrationTest
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/SampleServiceImplIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class SampleServiceImplIT {

	@Autowired
	private SampleService sampleService;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private SequencingObjectService objectService;
	@Autowired
	private AnalysisSubmissionService analysisSubmissionService;
	@Autowired
	private SampleGenomeAssemblyJoinRepository sampleGenomeAssemblyJoinRepository;
	@Autowired
	private MetadataTemplateService metadataTemplateService;

	/**
	 * Variation in a floating point number to be considered equal.
	 */
	private static final double deltaFloatEquality = 0.000001;

	private List<Long> sampleIds = List.of(1L, 2L);

	@Test
	@WithMockUser(username = "fbristow", roles = "ADMIN")
	public void testCreateSample() {
		Sample s = new Sample();
		String sampleName = "sampleName";
		s.setSampleName(sampleName);
		Sample saved = sampleService.create(s);
		assertEquals(sampleName, saved.getSampleName(), "Wrong name was saved.");
	}

	/**
	 * Straightforward merging of samples all belonging to the same project.
	 */
	@Test
	@WithMockUser(username = "fbristow", roles = "ADMIN")
	public void testMergeSamplesIntoSample1() {
		Sample mergeInto = sampleService.read(1L);
		Sample sample2 = sampleService.read(2L);
		Sample sample3 = sampleService.read(3L);
		Project p = projectService.read(1L);

		assertEquals(Lists.newArrayList(1L), sampleGenomeAssemblyJoinRepository.findBySample(mergeInto)
				.stream()
				.map(t -> t.getObject()
						.getId())
				.collect(Collectors.toList()), "Sample 1 should only have genome assembly 1");
		assertEquals(Lists.newArrayList(2L), sampleGenomeAssemblyJoinRepository.findBySample(sample2)
				.stream()
				.map(t -> t.getObject()
						.getId())
				.collect(Collectors.toList()), "Sample 2 should only have genome assembly 2");
		assertTrue(sampleGenomeAssemblyJoinRepository.findBySample(sample3)
				.isEmpty(), "Sample 3 should have no genome assemblies before");

		assertNotNull(sampleGenomeAssemblyJoinRepository.findBySampleAndAssemblyId(2L, 2L),
				"Join between sample 2 and genome assembly 2 should exist");

		Sample merged = sampleService.mergeSamples(p, mergeInto, Lists.newArrayList(sample2, sample3));

		assertEquals(mergeInto, merged, "Merged sample should be same as mergeInto.");

		// merged samples should be deleted
		assertSampleNotFound(2L);
		assertSampleNotFound(3L);

		assertNull(sampleGenomeAssemblyJoinRepository.findBySampleAndAssemblyId(2L, 2L),
				"Join between sample 2 and genome assembly 2 should not exist");

		// the merged sample should have 3 sequence files
		assertEquals(3, objectService.getSequencingObjectsForSample(merged)
				.size(), "Merged sample should have 3 sequence files");

		assertEquals(Lists.newArrayList(1L, 2L), sampleGenomeAssemblyJoinRepository.findBySample(mergeInto)
				.stream()
				.map(t -> t.getObject()
						.getId())
				.collect(Collectors.toList()), "Sample 1 should only have genome assemblies 1 and 2");
	}

	/**
	 * Straightforward merging of samples all belonging to the same project.
	 */
	@Test
	@WithMockUser(username = "fbristow", roles = "ADMIN")
	public void testMergeSamplesIntoSample3() {
		Sample mergeInto = sampleService.read(3L);
		Sample sample1 = sampleService.read(1L);
		Sample sample2 = sampleService.read(2L);
		Project p = projectService.read(1L);

		assertEquals(Lists.newArrayList(1L), sampleGenomeAssemblyJoinRepository.findBySample(sample1)
				.stream()
				.map(t -> t.getObject()
						.getId())
				.collect(Collectors.toList()), "Sample 1 should only have genome assembly 1");
		assertEquals(Lists.newArrayList(2L), sampleGenomeAssemblyJoinRepository.findBySample(sample2)
				.stream()
				.map(t -> t.getObject()
						.getId())
				.collect(Collectors.toList()), "Sample 2 should only have genome assembly 2");
		assertTrue(sampleGenomeAssemblyJoinRepository.findBySample(mergeInto)
				.isEmpty(), "Sample 3 should have no genome assemblies before");

		assertNotNull(sampleGenomeAssemblyJoinRepository.findBySampleAndAssemblyId(2L, 2L),
				"Join between sample 2 and genome assembly 2 should exist");
		assertNotNull(sampleGenomeAssemblyJoinRepository.findBySampleAndAssemblyId(1L, 1L),
				"Join between sample 1 and genome assembly 1 should exist");

		Sample merged = sampleService.mergeSamples(p, mergeInto, Lists.newArrayList(sample1, sample2));

		assertEquals(mergeInto, merged, "Merged sample should be same as mergeInto.");

		// merged samples should be deleted
		assertSampleNotFound(1L);
		assertSampleNotFound(2L);

		assertNull(sampleGenomeAssemblyJoinRepository.findBySampleAndAssemblyId(2L, 2L),
				"Join between sample 2 and genome assembly 2 should not exist");
		assertNull(sampleGenomeAssemblyJoinRepository.findBySampleAndAssemblyId(1L, 1L),
				"Join between sample 1 and genome assembly 1 should not exist");

		// the merged sample should have 3 sequence files
		assertEquals(3, objectService.getSequencingObjectsForSample(merged)
				.size(), "Merged sample should have 3 sequence files");

		assertEquals(Lists.newArrayList(1L, 2L), sampleGenomeAssemblyJoinRepository.findBySample(mergeInto)
				.stream()
				.map(t -> t.getObject()
						.getId())
				.collect(Collectors.toList()), "Sample 3 should only have genome assemblies 1 and 2");
	}

	/**
	 * Sample merging should be rejected when samples are attempted to be joined
	 * where they do not share the same project.
	 */
	@Test
	@WithMockUser(username = "fbristow", roles = "ADMIN")
	public void testMergeSampleReject() {
		Sample mergeInto = sampleService.read(1L);
		Project p = projectService.read(1L);
		assertThrows(IllegalArgumentException.class, () -> {
			sampleService.mergeSamples(p, mergeInto, Lists.newArrayList(sampleService.read(4L)));
		});
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "ADMIN")
	public void testGetSampleByExternalIdDuplicates() {
		Project p = projectService.read(7L);
		Sample s = sampleService.getSampleBySampleName(p, "sample");
		assertEquals(Long.valueOf(7L), s.getId(), "Should have retrieved sample with ID 1L.");
	}

	@WithMockUser(username = "fbristow", roles = "ADMIN")
	@Test
	public void testgetSampleByExternalNotFound() {
		Project p = projectService.read(1L);
		assertThrows(EntityNotFoundException.class, () -> {
			sampleService.getSampleBySampleName(p, "garbage");
		});
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "SEQUENCER")
	public void testReadSampleByExternalIdAsSequencer() {
		String externalId = "sample5";
		Project p = projectService.read(3L);
		Sample s = sampleService.getSampleBySampleName(p, externalId);

		assertNotNull(s, "Sample was not populated.");
		assertEquals(externalId, s.getSampleName(), "Wrong external id.");
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "SEQUENCER")
	public void testReadSampleAsSequencer() {
		Long sampleID = 1L;
		Sample s = sampleService.read(sampleID);

		assertNotNull(s, "Sample was not populated.");
		assertEquals(sampleID, s.getId(), "Wrong external id.");
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "SEQUENCER")
	public void testGetSampleForProjectAsSequencer() {
		Long sampleID = 2L;
		Long projectID = 1L;
		Project p = projectService.read(projectID);
		Sample s = sampleService.getSampleForProject(p, sampleID)
				.getObject();

		assertNotNull(s, "Sample was not populated.");
		assertEquals(sampleID, s.getId(), "Wrong external id.");
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "USER")
	public void testGetSampleForProjectAsUser() {
		Long sampleID = 2L;
		Sample s = sampleService.read(sampleID);

		assertNotNull(s, "Sample was not populated.");
		assertEquals(sampleID, s.getId(), "Wrong external id.");
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "ADMIN")
	public void testUpdateWithInvalidLatLong() {
		Sample s = sampleService.read(2L);
		s.setLatitude("not a geographic latitude");
		s.setLongitude("not a geographic longitude");

		assertThrows(ConstraintViolationException.class, () -> {
			sampleService.update(s);
		});
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "ADMIN")
	public void testUpdateWithInvalidRangeLatLong() {
		Sample s = sampleService.read(2L);
		s.setLatitude("-1000.00");
		s.setLongitude("1000.00");

		assertThrows(ConstraintViolationException.class, () -> {
			sampleService.update(s);
		});
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "ADMIN")
	public void testUpdateLatLong() {
		Sample s = sampleService.read(2L);

		String latitude = "50.00";
		String longitude = "-100.00";

		s.setLatitude(latitude);
		s.setLongitude(longitude);

		s = sampleService.update(s);
		assertEquals(latitude, s.getLatitude(), "Wrong latitude was stored.");
		assertEquals(longitude, s.getLongitude(), "Wrong longitude was stored.");
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "ADMIN")
	public void testGetSamplesForProjectWithName() {
		int pageSize = 2;
		Project project = projectService.read(1L);
		Page<ProjectSampleJoin> pageSamplesForProject = sampleService.getSamplesForProjectWithName(project, "", 0,
				pageSize, Direction.ASC, "createdDate");
		assertEquals(pageSize, pageSamplesForProject.getNumberOfElements());
		assertEquals(3, pageSamplesForProject.getTotalElements());

		pageSamplesForProject = sampleService.getSamplesForProjectWithName(project, "2", 0, pageSize, Direction.ASC,
				"createdDate");
		assertEquals(1, pageSamplesForProject.getTotalElements());
	}

	/**
	 * Tests getting the total bases for a sample as an admin user.
	 *
	 * @throws SequenceFileAnalysisException
	 */
	@Test
	@WithMockUser(username = "fbristow", roles = "ADMIN")
	public void testGetBasesForSample() throws SequenceFileAnalysisException {
		Long sampleID = 1L;
		Sample s = sampleService.read(sampleID);

		long bases = sampleService.getTotalBasesForSample(s);
		assertEquals(1000, bases);
	}

	/**
	 * Tests getting the total bases for a sample as a regular user.
	 *
	 * @throws SequenceFileAnalysisException
	 */
	@Test
	@WithMockUser(username = "fbristow", roles = "USER")
	public void testGetBasesForSampleAsUser() throws SequenceFileAnalysisException {
		Long sampleID = 1L;
		Sample s = sampleService.read(sampleID);

		long bases = sampleService.getTotalBasesForSample(s);
		assertEquals(1000, bases);
	}

	/**
	 * Tests failing to get bases for a sample for a user not on the project.
	 *
	 * @throws SequenceFileAnalysisException
	 */
	@Test
	@WithMockUser(username = "dr-evil", roles = "USER")
	public void testGetBasesForSampleInvalidUser() throws SequenceFileAnalysisException {
		Sample s = new Sample();
		s.setId(1L);

		assertThrows(AccessDeniedException.class, () -> {
			sampleService.getTotalBasesForSample(s);
		});
	}

	/**
	 * Tests failing to get coverage for a sample for a user not on the project.
	 *
	 * @throws SequenceFileAnalysisException
	 */
	@Test
	@WithMockUser(username = "dr-evil", roles = "USER")
	public void testEstimateCoverageForSampleInvalidUser() throws SequenceFileAnalysisException {
		Sample s = new Sample();
		s.setId(1L);

		assertThrows(AccessDeniedException.class, () -> {
			sampleService.estimateCoverageForSample(s, 500L);
		});
	}

	/**
	 * Tests getting the coverage as a regular user.
	 *
	 * @throws SequenceFileAnalysisException
	 */
	@Test
	@WithMockUser(username = "fbristow", roles = "USER")
	public void testEstimateCoverageForSampleAsUser() throws SequenceFileAnalysisException {
		Long sampleID = 1L;
		Sample s = sampleService.read(sampleID);

		double coverage = sampleService.estimateCoverageForSample(s, 500);
		assertEquals(2.0, coverage, deltaFloatEquality);
	}

	/**
	 * Tests esimating coverage with a reference file.
	 *
	 * @throws SequenceFileAnalysisException
	 */
	@Test
	@WithMockUser(username = "fbristow", roles = "USER")
	public void testEstimateCoverageForSampleReferenceFile() throws SequenceFileAnalysisException {
		Long sampleID = 1L;
		Sample s = sampleService.read(sampleID);

		ReferenceFile referenceFile = new ReferenceFile();
		referenceFile.setFileLength(500L);

		double coverage = sampleService.estimateCoverageForSample(s, referenceFile);
		assertEquals(2.0, coverage, deltaFloatEquality);
	}

	/**
	 * Tests failing to get the coverage for a sample with no fastqc results.
	 *
	 * @throws SequenceFileAnalysisException
	 */
	@Test
	@WithMockUser(username = "fbristow", roles = "ADMIN")
	public void testEstimateCoverageForSampleNoFastQC() throws SequenceFileAnalysisException {
		Long sampleID = 2L;
		Sample s = sampleService.read(sampleID);

		assertThrows(SequenceFileAnalysisException.class, () -> {
			sampleService.estimateCoverageForSample(s, 500);
		});
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "ADMIN")
	public void testGetSampleOrganismForProject() {
		Project p = projectService.read(1L);
		List<String> organisms = sampleService.getSampleOrganismsForProject(p);
		assertEquals(2, organisms.size(), "should be 2 organisms");
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "USER")
	public void testGetSamplesForAnalysisSubmission() {
		AnalysisSubmission submission = analysisSubmissionService.read(1L);
		Collection<Sample> samples = sampleService.getSamplesForAnalysisSubmission(submission);

		assertEquals(2, samples.size(), "should be 2 samples");

		Set<Long> ids = Sets.newHashSet(8L, 9L);
		samples.forEach(s -> ids.remove(s.getId()));

		assertTrue(ids.isEmpty(), "all sample ids should be found");
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "ADMIN")
	public void testGetQCEntiresForSample() {
		Sample s = sampleService.read(1L);
		List<QCEntry> qcEntriesForSample = sampleService.getQCEntriesForSample(s);

		assertEquals(1L, qcEntriesForSample.size(), "should be 1 qc entry");
	}

	@Test
	@WithMockUser(username = "dr-evil", roles = "USER")
	public void testGetQCEntiresForSampleNotAllowed() {
		Sample s = new Sample();
		s.setId(1L);
		assertThrows(AccessDeniedException.class, () -> {
			sampleService.getQCEntriesForSample(s);
		});
	}

	@Test
	@WithMockUser(username = "test", roles = "USER")
	public void testGetPartialMetadataAsUser() {
		Project project = projectService.read(1L);

		List<MetadataTemplateField> permittedFieldsForCurrentUser = metadataTemplateService.getPermittedFieldsForCurrentUser(
				project, true);


		ProjectMetadataResponse metadataForProject = sampleService.getMetadataForProjectSamples(project, sampleIds,
				permittedFieldsForCurrentUser);

		Map<Long, Set<MetadataEntry>> metadata = metadataForProject.getMetadata();

		Set<MetadataEntry> metadataEntries = metadata.values()
				.iterator()
				.next();

		assertEquals(1, metadataEntries.size(), "should only be 1 metadata entry");
		assertEquals("field1", metadataEntries.iterator()
				.next()
				.getField()
				.getLabel(), "only field1 should be available");
	}

	@Test
	@WithMockUser(username = "test", roles = "USER")
	public void testGetDisallowedMetadata() {
		Project project = projectService.read(1L);

		MetadataTemplateField field1 = metadataTemplateService.readMetadataField(1L);
		MetadataTemplateField field2 = metadataTemplateService.readMetadataField(
				2L); //user shouldn't be able to read this one in the project

		List<MetadataTemplateField> metadataTemplateFields = Lists.newArrayList(field1, field2);

		assertThrows(AccessDeniedException.class, () -> {
			sampleService.getMetadataForProjectSamples(project, sampleIds, metadataTemplateFields);
		});
	}

	@WithMockUser(username = "fbristow", roles = "MANAGER")
	@Test
	public void testManagerReadAllMetadata() {
		Project project = projectService.read(1L);

		MetadataTemplateField field1 = metadataTemplateService.readMetadataField(1L);
		MetadataTemplateField field2 = metadataTemplateService.readMetadataField(2L);

		List<MetadataTemplateField> metadataTemplateFields = Lists.newArrayList(field1, field2);

		ProjectMetadataResponse metadataForProject = sampleService.getMetadataForProjectSamples(project, sampleIds, metadataTemplateFields);

		Map<Long, Set<MetadataEntry>> metadata = metadataForProject.getMetadata();

		Set<MetadataEntry> metadataEntries = metadata.values()
				.iterator()
				.next();

		assertEquals(2, metadataEntries.size(), "should be 2 metadata entries");

		List<MetadataTemplateField> fields = metadataEntries.stream()
				.map(MetadataEntry::getField)
				.collect(Collectors.toList());

		assertEquals(2, fields.size(), "should be 2 fields");
		assertTrue(fields.contains(field1));
		assertTrue(fields.contains(field2));
	}

	@WithMockUser(username = "fbristow", roles = "MANAGER")
	@Test
	public void testReadSampleMetadata() {

		Sample sample = sampleService.read(1L);
		Set<MetadataEntry> metadataForSample = sampleService.getMetadataForSample(sample);

		assertEquals(2, metadataForSample.size(), "should be 2 entries");
	}

	@Test
	@WithMockUser(username = "test", roles = "USER")
	public void testReadSampleMetadataAsUser() {
		Sample sample = sampleService.read(1L);
		Set<MetadataEntry> metadataForSample = sampleService.getMetadataForSample(sample);

		assertEquals(1, metadataForSample.size(), "should be 1 entries");
	}

	private void assertSampleNotFound(Long id) {
		try {
			sampleService.read(id);
			fail("Merged sample with id [" + id + "] should be deleted.");
		} catch (EntityNotFoundException e) {
		} catch (Exception e) {
			fail("Failed for unknown reason; ");
		}
	}
}
