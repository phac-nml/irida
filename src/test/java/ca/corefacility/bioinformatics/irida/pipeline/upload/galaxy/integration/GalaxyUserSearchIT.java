package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

import ca.corefacility.bioinformatics.irida.config.IridaApiGalaxyTestConfig;
import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerObjectNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyUserNotFoundException;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyUserSearch;

import com.github.jmchilton.blend4j.galaxy.beans.User;
import com.github.springtestdbunit.DbUnitTestExecutionListener;

/**
 * Tests for searching for Galaxy Users.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiGalaxyTestConfig.class})
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
