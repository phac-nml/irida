package ca.corefacility.bioinformatics.irida.repositories.relational;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;

import javax.persistence.NonUniqueResultException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiRepositoriesConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.SampleRepository;
import ca.corefacility.bioinformatics.irida.utils.SecurityUser;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

/**
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiRepositoriesConfig.class,
		IridaApiTestDataSourceConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class SampleRelationalRepositoryTest {

	@Autowired
	private SampleRepository repo;

	@Autowired
	private ProjectRepository prepo;

	public SampleRelationalRepositoryTest() {
		SecurityUser.setUser();
	}

	/**
	 * Test of getSamplesForProject method, of class SampleRelationalRepository.
	 */
	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	public void testGetSamplesForProject() {
		Project read = prepo.read(1L);

		Collection<ProjectSampleJoin> projectsForUser = repo.getSamplesForProject(read);
		assertFalse(projectsForUser.isEmpty());

		for (ProjectSampleJoin join : projectsForUser) {
			assertTrue(join.getSubject().equals(read));
			assertNotNull(join.getSubject());
		}
	}

	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/repositories/relational/SampleRelationalRepositoryTest_duplicateSampleIds.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/repositories/relational/SampleRelationalRepositoryTest_duplicateSampleIds.xml")
	public void testGetSampleByExternalSampleIdDuplicates() {
		Project p = prepo.read(1l);

		try {
			Sample s = repo.getSampleByExternalSampleId(p, "external");
			assertEquals("Should have retrieved sample with ID 1L.", Long.valueOf(1), s.getId());
		} catch (NonUniqueResultException e) {
			fail("Only one sample should be returned as part of a project.");
		}
	}
}