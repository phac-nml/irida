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

import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyUserNotFoundException;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyUserSearch;

import com.github.jmchilton.blend4j.galaxy.UsersClient;
import com.github.jmchilton.blend4j.galaxy.beans.User;

/**
 * Tests for GalaxyUserSearch.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyUserSearchTest {

	@Mock private UsersClient usersClient;
	
	private GalaxyUserSearch galaxyUserSearch;
	
	private URL galaxyURL;
	
	/**
	 * Sets up objects for GalaxyUserSearchTest
	 * @throws MalformedURLException 
	 */
	@Before
	public void setup() throws MalformedURLException {
		MockitoAnnotations.initMocks(this);
		
		galaxyURL = new URL("http://localhost");
		
		User user1 = new User();
		user1.setEmail("user1@localhost");
		user1.setId("1");

		User user2 = new User();
		user2.setEmail("user2@localhost");
		user2.setId("2");

		List<User> userList = new ArrayList<User>();
		userList.add(user1);
		userList.add(user2);

		when(usersClient.getUsers()).thenReturn(userList);
		
		galaxyUserSearch = new GalaxyUserSearch(usersClient, galaxyURL);
	}
	
	/**
	 * Tests finding a user.
	 * @throws GalaxyUserNotFoundException 
	 */
	@Test
	public void testFindUserWithEmail() throws GalaxyUserNotFoundException {
		User foundUser = galaxyUserSearch.findUserWithEmail(new GalaxyAccountEmail(
				"user1@localhost"));
		assertNotNull(foundUser);
		assertEquals("user1@localhost", foundUser.getEmail());
		assertEquals("1", foundUser.getId());
	}

	/**
	 * Tests not finding a user.
	 * @throws GalaxyUserNotFoundException 
	 */
	@Test(expected=GalaxyUserNotFoundException.class)
	public void testNoFindUserWithEmail() throws GalaxyUserNotFoundException {
		galaxyUserSearch.findUserWithEmail(new GalaxyAccountEmail(
				"invalid@localhost"));
	}

	/**
	 * Tests if user exists.
	 */
	@Test
	public void testUserDoesExist() {
		assertTrue(galaxyUserSearch.galaxyUserExists(new GalaxyAccountEmail(
				"user1@localhost")));
	}

	/**
	 * Tests if user does not exist.
	 */
	@Test
	public void testUserDoesNotExist() {
		assertFalse(galaxyUserSearch.galaxyUserExists(new GalaxyAccountEmail(
				"invalid@localhost")));
	}
}
