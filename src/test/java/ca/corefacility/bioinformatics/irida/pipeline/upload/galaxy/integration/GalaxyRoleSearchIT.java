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
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyUserNoRoleException;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyRoleSearch;

import com.github.jmchilton.blend4j.galaxy.beans.Role;
import com.github.springtestdbunit.DbUnitTestExecutionListener;

/**
 * Tests for searching for Galaxy roles.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiGalaxyTestConfig.class})
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		DbUnitTestExecutionListener.class })
public class GalaxyRoleSearchIT {
	
	@Autowired
	private LocalGalaxy localGalaxy;
	
	private GalaxyRoleSearch galaxyRoleSearch;
	
	/**
	 * Sets up objects for GalaxyRoleSearch.
	 */
	@Before
	public void setup() {
		galaxyRoleSearch = new GalaxyRoleSearch(localGalaxy.getGalaxyInstanceAdmin().getRolesClient(),
								localGalaxy.getGalaxyURL());
	}
	
	/**
	 * Tests for a role that exists.
	 */
	@Test
	public void testGalaxyRoleExists() {
		assertTrue(galaxyRoleSearch.exists(localGalaxy.getAdminName()));
	}
	
	/**
	 * Tests for a role that does not exist.
	 */
	@Test
	public void testGalaxyRoleDoesNotExist() {
		assertFalse(galaxyRoleSearch.exists(localGalaxy.getNonExistentGalaxyAdminName()));
	}
	
	/**
	 * Tests successfull finding a role by email.
	 * @throws ExecutionManagerObjectNotFoundException 
	 */
	@Test
	public void testFindUserRoleWithEmailSuccess() throws ExecutionManagerObjectNotFoundException {
		Role role = galaxyRoleSearch.findById(localGalaxy.getAdminName());
		assertEquals(localGalaxy.getAdminName().getName(),role.getName());
	}
	
	/**
	 * Tests failure to find a role by email.
	 * @throws ExecutionManagerObjectNotFoundException 
	 */
	@Test(expected=GalaxyUserNoRoleException.class)
	public void testFindUserRoleWithEmailFailure() throws ExecutionManagerObjectNotFoundException {
		galaxyRoleSearch.findById(localGalaxy.getNonExistentGalaxyAdminName());
	}
}
