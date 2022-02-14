package ca.corefacility.bioinformatics.irida.ria.integration.users;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.user.UserDetailsPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/users/UserDetailsPageIT.xml")
public class UserDetailsPageIT extends AbstractIridaUIITChromeDriver {
	private UserDetailsPage usersPage;

	@BeforeEach
	public void setUpTest() {
		LoginPage.loginAsManager(driver());
		usersPage = new UserDetailsPage(driver());
	}

	@Test
	public void testGetUserId() {
		usersPage.getCurrentUser();
		String currentUserId = usersPage.getUserId();
		assertEquals(String.valueOf(1L), currentUserId);
	}

	@Test
	public void testGetOtherUserId() {
		Long id = 2L;
		usersPage.getOtherUser(id);
		String otherUserId = usersPage.getUserId();
		assertEquals(String.valueOf(id), otherUserId);
	}

	@Test
	public void testGetEditUserButton() {
		assertTrue(usersPage.canGetEditLink(1L), "Should see button for user 1");
		assertFalse(usersPage.canGetEditLink(2L), "Should not see button for user 2");
	}

	@Test
	public void testGetUserProjects() {
		usersPage.getOtherUser(1L);
		List<String> userProjectIds = usersPage.getUserProjectIds();
		assertTrue(userProjectIds.contains("1"));
		assertTrue(userProjectIds.contains("2"));
	}

	@Test
	public void testResetUserPassword() {
		usersPage.getOtherUser(1L);
		usersPage.sendPasswordReset();
		assertTrue(usersPage.checkSuccessNotification());
	}

	@Test
	public void testSubscribeToProject() {
		usersPage.getCurrentUser();
		usersPage.subscribeToFirstProject();
		assertTrue(usersPage.checkSuccessNotification(), "Should be a success notification that you subscribed");
	}

}
