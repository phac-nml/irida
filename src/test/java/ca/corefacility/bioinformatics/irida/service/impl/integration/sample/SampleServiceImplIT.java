package ca.corefacility.bioinformatics.irida.service.impl.integration.sample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.Map;

import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExcecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.config.IridaApiNoGalaxyTestConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.processing.IridaApiTestMultithreadingConfig;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.repositories.specification.ProjectSampleFilterSpecification;
import ca.corefacility.bioinformatics.irida.repositories.specification.ProjectSampleJoinSpecification;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.ImmutableMap;

/**
 * Integration tests for the sample service.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiNoGalaxyTestConfig.class, IridaApiTestDataSourceConfig.class, IridaApiTestMultithreadingConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExcecutionListener.class})
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/SampleServiceImplIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class SampleServiceImplIT {

	@Autowired
	private SampleService sampleService;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private SequenceFileService sequenceFileService;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	@WithMockUser(username = "fbristow", roles = "ADMIN")
	public void testCreateSample() {
		Sample s = new Sample();
		String sampleName = "sampleName";
		s.setSampleName(sampleName);
		s.setSequencerSampleId("sampleId");
		Sample saved = sampleService.create(s);
		assertEquals("Wrong name was saved.", sampleName, saved.getSampleName());
	}

	/**
	 * Straightforward merging of samples all belonging to the same project.
	 */
	@Test
	@WithMockUser(username = "fbristow", roles = "ADMIN")
	public void testMergeSamples() {
		Sample mergeInto = sampleService.read(1l);
		Project p = projectService.read(1l);

		Sample merged = sampleService.mergeSamples(p, mergeInto, sampleService.read(2l), sampleService.read(3l));

		assertEquals("Merged sample should be same as mergeInto.", mergeInto, merged);

		// merged samples should be deleted
		assertSampleNotFound(2l);
		assertSampleNotFound(3l);

		// the merged sample should have 3 sequence files
		assertEquals("Merged sample should have 3 sequence files", 3,
				sequenceFileService.getSequenceFilesForSample(merged).size());
	}

	/**
	 * Sample merging should be rejected when samples are attempted to be joined where they do not share the same
	 * project.
	 */
	@Test(expected = IllegalArgumentException.class)
	@WithMockUser(username = "fbristow", roles = "ADMIN")
	public void testMergeSampleReject() {
		Sample mergeInto = sampleService.read(1l);
		Project p = projectService.read(1l);
		sampleService.mergeSamples(p, mergeInto, sampleService.read(4l));
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "ADMIN")
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/SampleServiceImplIT_duplicateSampleIds.xml")
	public void testGetSampleByExternalIdDuplicates() {
		Project p = projectService.read(7l);
		Sample s = sampleService.getSampleBySequencerSampleId(p, "external");
		assertEquals("Should have retrieved sample with ID 1L.", Long.valueOf(7l), s.getId());
	}

	@WithMockUser(username = "fbristow", roles = "ADMIN")
	@Test(expected = EntityNotFoundException.class)
	public void testgetSampleByExternalNotFound() {
		Project p = projectService.read(1l);
		sampleService.getSampleBySequencerSampleId(p, "garbage");
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "SEQUENCER")
	public void testReadSampleByExternalIdAsSequencer() {
		String externalId = "sample5";
		Project p = projectService.read(3L);
		Sample s = sampleService.getSampleBySequencerSampleId(p, externalId);

		assertNotNull("Sample was not populated.", s);
		assertEquals("Wrong external id.", externalId, s.getSequencerSampleId());
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
		Sample s = sampleService.getSampleForProject(p, sampleID);

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
		Long sampleId = 2L;
		Map<String, Object> properties = ImmutableMap.of("latitude", "not a geographic latitude", "longitude",
				"not a geographic longitude");

		sampleService.update(sampleId, properties);
	}

	@Test(expected = ConstraintViolationException.class)
	@WithMockUser(username = "fbristow", roles = "ADMIN")
	public void testUpdateWithInvalidRangeLatLong() {
		Long sampleId = 2L;
		Map<String, Object> properties = ImmutableMap.of("latitude", "-1000.00", "longitude", "1000.00");
		sampleService.update(sampleId, properties);
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "ADMIN")
	public void testUpdateLatLong() {
		Long sampleId = 2L;
		String latitude = "50.00";
		String longitude = "-100.00";
		Map<String, Object> properties = ImmutableMap.of("latitude", latitude, "longitude", longitude);
		Sample s = sampleService.update(sampleId, properties);
		assertEquals("Wrong latitude was stored.", latitude, s.getLatitude());
		assertEquals("Wrong longitude was stored.", longitude, s.getLongitude());
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "ADMIN")
	public void testGetSamplesForProjectWithName() {
		int pageSize = 2;
		Project project = projectService.read(1l);
		Page<ProjectSampleJoin> pageSamplesForProject = sampleService.getSamplesForProjectWithName(project, "", 0,
				pageSize, Direction.ASC, "createdDate");
		assertEquals(pageSize, pageSamplesForProject.getNumberOfElements());
		assertEquals(3, pageSamplesForProject.getTotalElements());

		pageSamplesForProject = sampleService.getSamplesForProjectWithName(project, "2", 0, pageSize, Direction.ASC,
				"createdDate");
		assertEquals(1, pageSamplesForProject.getTotalElements());
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "ADMIN")
	public void testSearchProjectSamples() {
		int pageSize = 2;
		Project project = projectService.read(1l);
		Page<ProjectSampleJoin> pageSamplesForProject = sampleService.searchProjectSamples(
				ProjectSampleJoinSpecification.searchSampleWithNameInProject("", project), 0, pageSize, Direction.ASC,
				"createdDate");
		assertEquals(pageSize, pageSamplesForProject.getNumberOfElements());
		assertEquals(3, pageSamplesForProject.getTotalElements());

		pageSamplesForProject = sampleService.searchProjectSamples(
				ProjectSampleJoinSpecification.searchSampleWithNameInProject("2", project), 0, pageSize, Direction.ASC,
				"createdDate");
		assertEquals(1, pageSamplesForProject.getTotalElements());

	}

	@Test
	@WithMockUser(username = "fbristow", roles = "ADMIN")
	public void testFilterProjectSamples() {
		int pageSize = 2;
		Project project = projectService.read(1l);

		// Check with no filters.
		Specification<ProjectSampleJoin> specification = ProjectSampleFilterSpecification.searchProjectSamples(project, "", "", null, null);
		Page<ProjectSampleJoin> page = sampleService.searchProjectSamples(specification, 0, pageSize, Direction.ASC, "createdDate");
		assertEquals(pageSize, page.getNumberOfElements());
		assertEquals(3, page.getTotalElements());

		// Check with a name filter
		specification = ProjectSampleFilterSpecification.searchProjectSamples(project, "2", "", null, null);
		page = sampleService.searchProjectSamples(specification, 0, pageSize, Direction.ASC, "createdDate");
		assertEquals(1, page.getTotalElements());

		// Check with a name that does not exist
		specification = ProjectSampleFilterSpecification.searchProjectSamples(project, "FRED_PENNER", "", null, null);
		page = sampleService.searchProjectSamples(specification, 0, pageSize, Direction.ASC, "createdDate");
		assertEquals(0, page.getTotalElements());

		// Check with a min date filter
		specification = ProjectSampleFilterSpecification.searchProjectSamples(project, "", "", new Date(1363634419000L), null);
		page = sampleService.searchProjectSamples(specification, 0, pageSize, Direction.ASC, "createdDate");
		assertEquals(3, page.getNumberOfElements());

		// Check with max date filter
		specification = ProjectSampleFilterSpecification.searchProjectSamples(project, "", "", null, new Date(1366312819000L));
		page = sampleService.searchProjectSamples(specification, 0, pageSize, Direction.ASC, "createdDate");
		assertEquals(3, page.getNumberOfElements());

		// Check with min and max date filter
		specification = ProjectSampleFilterSpecification.searchProjectSamples(project, "", "", new Date(1363634419000L), new Date(1366312819000L));
		page = sampleService.searchProjectSamples(specification, 0, pageSize, Direction.ASC, "createdDate");
		assertEquals(1, page.getNumberOfElements());
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
