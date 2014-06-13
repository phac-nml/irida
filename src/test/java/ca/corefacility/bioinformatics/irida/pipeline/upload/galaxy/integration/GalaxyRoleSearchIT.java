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

import ca.corefacility.bioinformatics.irida.config.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy.NonWindowsLocalGalaxyConfig;
import ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy.WindowsLocalGalaxyConfig;
import ca.corefacility.bioinformatics.irida.config.processing.IridaApiTestMultithreadingConfig;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyUserNoRoleException;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyRoleSearch;

import com.github.jmchilton.blend4j.galaxy.beans.Role;
import com.github.springtestdbunit.DbUnitTestExecutionListener;

/**
 * Tests for searching for Galaxy roles.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {
		IridaApiServicesConfig.class, IridaApiTestDataSourceConfig.class,
		IridaApiTestMultithreadingConfig.class, NonWindowsLocalGalaxyConfig.class, WindowsLocalGalaxyConfig.class  })
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
		assertTrue(galaxyRoleSearch.userRoleExistsFor(localGalaxy.getAdminName()));
	}
	
	/**
	 * Tests for a role that does not exist.
	 */
	@Test
	public void testGalaxyRoleDoesNotExist() {
		assertFalse(galaxyRoleSearch.userRoleExistsFor(localGalaxy.getNonExistentGalaxyAdminName()));
	}
	
	/**
	 * Tests successfull finding a role by email.
	 * @throws GalaxyUserNoRoleException 
	 */
	@Test
	public void testFindUserRoleWithEmailSuccess() throws GalaxyUserNoRoleException {
		Role role = galaxyRoleSearch.findUserRoleWithEmail(localGalaxy.getAdminName());
		assertEquals(localGalaxy.getAdminName().getName(),role.getName());
	}
	
	/**
	 * Tests failure to find a role by email.
	 * @throws GalaxyUserNoRoleException 
	 */
	@Test(expected=GalaxyUserNoRoleException.class)
	public void testFindUserRoleWithEmailFailure() throws GalaxyUserNoRoleException {
		galaxyRoleSearch.findUserRoleWithEmail(localGalaxy.getNonExistentGalaxyAdminName());
	}
}
