package ca.corefacility.bioinformatics.irida.service.impl.integration.sample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.exceptions.AnalysisAlreadySetException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.SequenceFileAnalysisException;
import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleGenomeAssemblyJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sample.QCEntry;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleGenomeAssemblyJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.SampleRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

/**
 * Integration tests for the sample service.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiJdbcDataSourceConfig.class })
@ActiveProfiles("it")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExecutionListener.class })
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
	private Path outputFileBaseDirectory;
	@Autowired
	private SampleGenomeAssemblyJoinRepository sampleGenomeAssemblyJoinRepository;
	@Autowired
	private SampleRepository sampleRepository;

	/**
	 * Variation in a floating point number to be considered equal.
	 */
	private static final double deltaFloatEquality = 0.000001;

	@Test
	@WithMockUser(username = "fbristow", roles = "ADMIN")
	public void testCreateSample() {
		Sample s = new Sample();
		String sampleName = "sampleName";
		s.setSampleName(sampleName);
		Sample saved = sampleService.create(s);
		assertEquals("Wrong name was saved.", sampleName, saved.getSampleName());
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

		assertEquals("Sample 1 should only have genome assembly 1", Lists.newArrayList(1L),
				sampleGenomeAssemblyJoinRepository.findBySample(mergeInto).stream().map(t -> t.getObject().getId())
						.collect(Collectors.toList()));
		assertEquals("Sample 2 should only have genome assembly 2", Lists.newArrayList(2L),
				sampleGenomeAssemblyJoinRepository.findBySample(sample2).stream().map(t -> t.getObject().getId())
						.collect(Collectors.toList()));
		assertTrue("Sample 3 should have no genome assemblies before",
				sampleGenomeAssemblyJoinRepository.findBySample(sample3).isEmpty());

		assertNotNull("Join between sample 2 and genome assembly 2 should exist",
				sampleGenomeAssemblyJoinRepository.findBySampleAndAssemblyId(2L, 2L));

		Sample merged = sampleService.mergeSamples(p, mergeInto, Lists.newArrayList(sample2, sample3));

		assertEquals("Merged sample should be same as mergeInto.", mergeInto, merged);

		// merged samples should be deleted
		assertSampleNotFound(2L);
		assertSampleNotFound(3L);

		assertNull("Join between sample 2 and genome assembly 2 should not exist",
				sampleGenomeAssemblyJoinRepository.findBySampleAndAssemblyId(2L, 2L));

		// the merged sample should have 3 sequence files
		assertEquals("Merged sample should have 3 sequence files", 3,
				objectService.getSequencingObjectsForSample(merged).size());

		assertEquals("Sample 1 should only have genome assemblies 1 and 2", Lists.newArrayList(1L, 2L),
				sampleGenomeAssemblyJoinRepository.findBySample(mergeInto).stream().map(t -> t.getObject().getId())
						.collect(Collectors.toList()));
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

		assertEquals("Sample 1 should only have genome assembly 1", Lists.newArrayList(1L),
				sampleGenomeAssemblyJoinRepository.findBySample(sample1).stream().map(t -> t.getObject().getId())
						.collect(Collectors.toList()));
		assertEquals("Sample 2 should only have genome assembly 2", Lists.newArrayList(2L),
				sampleGenomeAssemblyJoinRepository.findBySample(sample2).stream().map(t -> t.getObject().getId())
						.collect(Collectors.toList()));
		assertTrue("Sample 3 should have no genome assemblies before",
				sampleGenomeAssemblyJoinRepository.findBySample(mergeInto).isEmpty());

		assertNotNull("Join between sample 2 and genome assembly 2 should exist",
				sampleGenomeAssemblyJoinRepository.findBySampleAndAssemblyId(2L, 2L));
		assertNotNull("Join between sample 1 and genome assembly 1 should exist",
				sampleGenomeAssemblyJoinRepository.findBySampleAndAssemblyId(1L, 1L));

		Sample merged = sampleService.mergeSamples(p, mergeInto, Lists.newArrayList(sample1, sample2));

		assertEquals("Merged sample should be same as mergeInto.", mergeInto, merged);

		// merged samples should be deleted
		assertSampleNotFound(1L);
		assertSampleNotFound(2L);

		assertNull("Join between sample 2 and genome assembly 2 should not exist",
				sampleGenomeAssemblyJoinRepository.findBySampleAndAssemblyId(2L, 2L));
		assertNull("Join between sample 1 and genome assembly 1 should not exist",
				sampleGenomeAssemblyJoinRepository.findBySampleAndAssemblyId(1L, 1L));

		// the merged sample should have 3 sequence files
		assertEquals("Merged sample should have 3 sequence files", 3,
				objectService.getSequencingObjectsForSample(merged).size());

		assertEquals("Sample 3 should only have genome assemblies 1 and 2", Lists.newArrayList(1L, 2L),
				sampleGenomeAssemblyJoinRepository.findBySample(mergeInto).stream().map(t -> t.getObject().getId())
						.collect(Collectors.toList()));
	}

	/**
	 * Sample merging should be rejected when samples are attempted to be joined
	 * where they do not share the same project.
	 */
	@Test(expected = IllegalArgumentException.class)
	@WithMockUser(username = "fbristow", roles = "ADMIN")
	public void testMergeSampleReject() {
		Sample mergeInto = sampleService.read(1L);
		Project p = projectService.read(1L);
		sampleService.mergeSamples(p, mergeInto, Lists.newArrayList(sampleService.read(4L)));
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "ADMIN")
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/SampleServiceImplIT_duplicateSampleIds.xml")
	public void testGetSampleByExternalIdDuplicates() {
		Project p = projectService.read(7L);
		Sample s = sampleService.getSampleBySampleName(p, "sample");
		assertEquals("Should have retrieved sample with ID 1L.", Long.valueOf(7L), s.getId());
	}

	@WithMockUser(username = "fbristow", roles = "ADMIN")
	@Test(expected = EntityNotFoundException.class)
	public void testgetSampleByExternalNotFound() {
		Project p = projectService.read(1L);
		sampleService.getSampleBySampleName(p, "garbage");
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "SEQUENCER")
	public void testReadSampleByExternalIdAsSequencer() {
		String externalId = "sample5";
		Project p = projectService.read(3L);
		Sample s = sampleService.getSampleBySampleName(p, externalId);

		assertNotNull("Sample was not populated.", s);
		assertEquals("Wrong external id.", externalId, s.getSampleName());
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "SEQUENCER")
	public void testReadSampleAsSequencer() {
		Long sampleID = 1L;
		Sample s = sampleService.read(sampleID);

		assertNotNull("Sample was not populated.", s);
		assertEquals("Wrong external id.", sampleID, s.getId());
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "SEQUENCER")
	public void testGetSampleForProjectAsSequencer() {
		Long sampleID = 2L;
		Long projectID = 1L;
		Project p = projectService.read(projectID);
		Sample s = sampleService.getSampleForProject(p, sampleID).getObject();

		assertNotNull("Sample was not populated.", s);
		assertEquals("Wrong external id.", sampleID, s.getId());
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "USER")
	public void testGetSampleForProjectAsUser() {
		Long sampleID = 2L;
		Sample s = sampleService.read(sampleID);

		assertNotNull("Sample was not populated.", s);
		assertEquals("Wrong external id.", sampleID, s.getId());
	}

	@Test(expected = ConstraintViolationException.class)
	@WithMockUser(username = "fbristow", roles = "ADMIN")
	public void testUpdateWithInvalidLatLong() {
		Sample s = sampleService.read(2L);
		s.setLatitude("not a geographic latitude");
		s.setLongitude("not a geographic longitude");

		sampleService.update(s);
	}

	@Test(expected = ConstraintViolationException.class)
	@WithMockUser(username = "fbristow", roles = "ADMIN")
	public void testUpdateWithInvalidRangeLatLong() {
		Sample s = sampleService.read(2L);
		s.setLatitude("-1000.00");
		s.setLongitude("1000.00");

		sampleService.update(s);
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
		assertEquals("Wrong latitude was stored.", latitude, s.getLatitude());
		assertEquals("Wrong longitude was stored.", longitude, s.getLongitude());
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
	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "dr-evil", roles = "USER")
	public void testGetBasesForSampleInvalidUser() throws SequenceFileAnalysisException {
		Sample s = new Sample();
		s.setId(1L);

		sampleService.getTotalBasesForSample(s);
	}

	/**
	 * Tests failing to get coverage for a sample for a user not on the project.
	 * 
	 * @throws SequenceFileAnalysisException
	 */
	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "dr-evil", roles = "USER")
	public void testEstimateCoverageForSampleInvalidUser() throws SequenceFileAnalysisException {
		Sample s = new Sample();
		s.setId(1L);

		sampleService.estimateCoverageForSample(s, 500L);
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
	@Test(expected = SequenceFileAnalysisException.class)
	@WithMockUser(username = "fbristow", roles = "ADMIN")
	public void testEstimateCoverageForSampleNoFastQC() throws SequenceFileAnalysisException {
		Long sampleID = 2L;
		Sample s = sampleService.read(sampleID);

		sampleService.estimateCoverageForSample(s, 500);
	}
	
	@Test
	@WithMockUser(username = "fbristow", roles = "ADMIN")
	public void testGetSampleOrganismForProject(){
		Project p = projectService.read(1L);
		List<String> organisms = sampleService.getSampleOrganismsForProject(p);
		assertEquals("should be 2 organisms", 2, organisms.size());
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "USER")
	public void testGetSamplesForAnalysisSubmission() {
		AnalysisSubmission submission = analysisSubmissionService.read(1L);
		Collection<Sample> samples = sampleService.getSamplesForAnalysisSubmission(submission);
		
		assertEquals("should be 2 samples", 2, samples.size());
		
		Set<Long> ids = Sets.newHashSet(8L, 9L);
		samples.forEach(s -> ids.remove(s.getId()));
		
		assertTrue("all sample ids should be found", ids.isEmpty());
	}
	
	@Test
	@WithMockUser(username = "fbristow", roles = "ADMIN")
	public void testGetQCEntiresForSample() {
		Sample s = sampleService.read(1L);
		List<QCEntry> qcEntriesForSample = sampleService.getQCEntriesForSample(s);

		assertEquals("should be 1 qc entry", 1L, qcEntriesForSample.size());
	}
	
	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "dr-evil", roles = "USER")
	public void testGetQCEntiresForSampleNotAllowed() {
		Sample s = new Sample();
		s.setId(1L);
		sampleService.getQCEntriesForSample(s);
	}
	
	@Test
	@WithMockUser(username = "fbristow", roles="USER")
	public void testGetGenomeAssemblyForSampleSuccess() throws AnalysisAlreadySetException {
		Path expectedAssemblyPath = outputFileBaseDirectory.resolve("testfile.fasta");
		Sample s = sampleService.read(1L);
				
		GenomeAssembly genomeAssembly = sampleService.getGenomeAssemblyForSample(s, 1L);
		assertEquals("should have same path for assembly", expectedAssemblyPath, genomeAssembly.getFile());
	}
	
	@Test(expected=EntityNotFoundException.class)
	@WithMockUser(username = "fbristow", roles="USER")
	public void testGetGenomeAssemblyForSampleFailNoAssembly() {
		Sample s = sampleService.read(1L);
		sampleService.getGenomeAssemblyForSample(s, 2L);
	}
	
	@Test(expected=AccessDeniedException.class)
	@WithMockUser(username = "dr-evil", roles="USER")
	public void testGetGenomeAssemblyForSampleFailDenied() {
		Sample s = sampleService.read(1L);
		sampleService.getGenomeAssemblyForSample(s, 1L);
	}
	
	@Test
	@WithMockUser(username = "fbristow", roles="USER")
	public void testGetAssembliesForSampleSuccess() {
		Sample s = sampleService.read(1L);
		Collection<SampleGenomeAssemblyJoin> joins = sampleService.getAssembliesForSample(s);
		assertEquals("should have same size for assemblies", 1, joins.size());
		
		SampleGenomeAssemblyJoin join = joins.iterator().next();
		assertEquals("Should be same sample", s.getId(), join.getSubject().getId());
		assertEquals("Should be same assembly", new Long(1L), join.getObject().getId());
	}
	
	@Test(expected=AccessDeniedException.class)
	@WithMockUser(username = "dr-evil", roles="USER")
	public void testGetAssembliesForSampleFail() {
		Sample s = sampleService.read(1L);
		sampleService.getAssembliesForSample(s);
	}
	
	@Test
	@WithMockUser(username = "fbristow", roles="USER")
	public void testRemoveGenomeAssemblyFromSampleSuccess() {	
		Sample s = sampleService.read(1L);
		assertNotNull(sampleService.getGenomeAssemblyForSample(s, 1L));
		
		sampleService.removeGenomeAssemblyFromSample(s, 1L);
		
		try {
			sampleService.getGenomeAssemblyForSample(s, 1L);
		} catch (EntityNotFoundException e) {
			return;
		}
		fail("Did not catch " + EntityNotFoundException.class);
	}
	
	@Test(expected=AccessDeniedException.class)
	@WithMockUser(username = "dr-evil", roles="USER")
	public void testRemoveGenomeAssemblyFromSampleFail() {	
		Sample s = sampleService.read(1L);
		assertNotNull(sampleService.getGenomeAssemblyForSample(s, 1L));
		
		sampleService.removeGenomeAssemblyFromSample(s, 1L);
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
