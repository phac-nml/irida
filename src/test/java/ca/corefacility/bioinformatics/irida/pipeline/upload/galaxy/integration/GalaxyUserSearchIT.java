package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.config.analysis.AnalysisExecutionServiceTestConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy.NonWindowsLocalGalaxyConfig;
import ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy.WindowsLocalGalaxyConfig;
import ca.corefacility.bioinformatics.irida.config.processing.IridaApiTestMultithreadingConfig;
import ca.corefacility.bioinformatics.irida.config.workflow.RemoteWorkflowServiceTestConfig;
import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerObjectNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyUserNotFoundException;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyUserSearch;

import com.github.jmchilton.blend4j.galaxy.beans.User;
import com.github.springtestdbunit.DbUnitTestExecutionListener;

/**
 * Tests for searching for Galaxy Users.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {
	IridaApiServicesConfig.class, IridaApiTestDataSourceConfig.class,
	IridaApiTestMultithreadingConfig.class, NonWindowsLocalGalaxyConfig.class,
	WindowsLocalGalaxyConfig.class, AnalysisExecutionServiceTestConfig.class,
	RemoteWorkflowServiceTestConfig.class})
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		DbUnitTestExecutionListener.class })
public class GalaxyUserSearchIT {
	
	@Autowired
	private LocalGalaxy localGalaxy;
	
	private GalaxyUserSearch galaxyUserSearch;
	
	/**
	 * Sets up objects for GalaxyUserSearch.
	 */
	@Before
	public void setup() {
		galaxyUserSearch = new GalaxyUserSearch(localGalaxy.getGalaxyInstanceAdmin().getUsersClient(),
								localGalaxy.getGalaxyURL());
	}
	
	/**
	 * Tests for a user that exists.
	 */
	@Test
	public void testGalaxyUserExists() {
		assertTrue(galaxyUserSearch.exists(localGalaxy.getAdminName()));
	}
	
	/**
	 * Tests for a user that does not exist.
	 */
	@Test
	public void testGalaxyUserDoesNotExist() {
		assertFalse(galaxyUserSearch.exists(localGalaxy.getNonExistentGalaxyAdminName()));
	}
	
	/**
	 * Tests successfull finding a user by email.
	 * @throws ExecutionManagerObjectNotFoundException 
	 */
	@Test
	public void testFindUserWithEmailSuccess() throws ExecutionManagerObjectNotFoundException {
		User user = galaxyUserSearch.findById(localGalaxy.getAdminName());
		assertEquals(localGalaxy.getAdminName().getName(),user.getEmail());
	}
	
	/**
	 * Tests failure to find a user by email.
	 * @throws ExecutionManagerObjectNotFoundException 
	 */
	@Test(expected=GalaxyUserNotFoundException.class)
	public void testFindUserWithEmailFail() throws ExecutionManagerObjectNotFoundException {
		galaxyUserSearch.findById(localGalaxy.getNonExistentGalaxyAdminName());
	}
}
