package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerObjectNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyUserNoRoleException;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyRoleSearch;

import com.github.jmchilton.blend4j.galaxy.RolesClient;
import com.github.jmchilton.blend4j.galaxy.beans.Role;

/**
 * Tests for the GalaxyRoleSearch class.
 *
 */
public class GalaxyRoleSearchTest {
	
	@Mock private RolesClient rolesClient;
	
	private GalaxyRoleSearch galaxyRoleSearch;
	private URL galaxyURL;
	
	/**
	 * Setup objects for test.
	 * @throws MalformedURLException 
	 */
	@Before
	public void setup() throws MalformedURLException {
		MockitoAnnotations.initMocks(this);
		
		galaxyURL = new URL("http://localhost");
		
		galaxyRoleSearch = new GalaxyRoleSearch(rolesClient, galaxyURL);

		Role role1 = new Role();
		role1.setName("role1@localhost");
		role1.setId("1");

		Role role2 = new Role();
		role2.setName("role2@localhost");
		role2.setId("2");

		List<Role> roleList = new ArrayList<Role>();
		roleList.add(role1);
		roleList.add(role2);

		when(rolesClient.getRoles()).thenReturn(roleList);
	}
	
	/**
	 * Tests finding a user role.
	 * @throws ExecutionManagerObjectNotFoundException 
	 */
	@Test
	public void testFindByIdSuccess() throws ExecutionManagerObjectNotFoundException {
		Role foundRole = galaxyRoleSearch.findById(new GalaxyAccountEmail("role1@localhost"));
		assertNotNull(foundRole);
		assertEquals("role1@localhost", foundRole.getName());
		assertEquals("1", foundRole.getId());
	}

	/**
	 * Tests no finding a user role.
	 * @throws ExecutionManagerObjectNotFoundException 
	 */
	@Test(expected=GalaxyUserNoRoleException.class)
	public void testFindByIdFail() throws ExecutionManagerObjectNotFoundException {
		galaxyRoleSearch.findById(new GalaxyAccountEmail("invalid@localhost"));
	}
	
	/**
	 * Tests finding a null user role
	 * @throws ExecutionManagerObjectNotFoundException 
	 */
	@Test(expected=NullPointerException.class)
	public void testFindByIdNull() throws ExecutionManagerObjectNotFoundException {
		galaxyRoleSearch.findById(null);
	}
	
	/**
	 * Tests finding a user role.
	 */
	@Test
	public void testExistsSuccess() {
		assertTrue(galaxyRoleSearch.exists(new GalaxyAccountEmail("role1@localhost")));
	}
	
	/**
	 * Tests not finding a user role.
	 */
	@Test
	public void testExistsFail() {
		assertFalse(galaxyRoleSearch.exists(new GalaxyAccountEmail("invalid@localhost")));
	}
}
