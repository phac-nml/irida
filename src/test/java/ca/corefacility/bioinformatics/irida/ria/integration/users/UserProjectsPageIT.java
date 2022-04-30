package ca.corefacility.bioinformatics.irida.ria.integration.users;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.user.UserProjectsPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/users/UserProjectsPageIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class UserProjectsPageIT extends AbstractIridaUIITChromeDriver {
	private UserProjectsPage userPage;

	@BeforeEach
	public void setUpTest() {
		LoginPage.loginAsManager(driver());
		userPage = new UserProjectsPage(driver());
	}

	@Test
	public void testGetUserProjects() {
		userPage.goTo();
		List<String> userProjectIds = userPage.getUserProjectIds();
		assertTrue(userProjectIds.contains("1"));
		assertTrue(userProjectIds.contains("2"));
	}

	@Test
	public void testSubscribeToProject() {
		userPage.goTo();
		userPage.subscribeToFirstProject();
		assertTrue(userPage.checkSuccessNotification(), "Should be a success notification that you subscribed");
	}

}
