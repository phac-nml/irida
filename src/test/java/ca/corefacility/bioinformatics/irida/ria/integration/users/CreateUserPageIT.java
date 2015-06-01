package ca.corefacility.bioinformatics.irida.ria.integration.users;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIIT;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.user.CreateUserPage;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/users/CreateUserPageIT.xml")
public class CreateUserPageIT extends AbstractIridaUIIT {
	private CreateUserPage createPage;

	@Before
	public void setUpTest() {
		LoginPage.loginAsManager(driver());
		createPage = new CreateUserPage(driver());
	}

	@Test
	public void createGoodUser() {
		createPage.goTo();
		createPage.createUserWithPassword("tom", "tom@somwehre.com", "Password1", "Password1");
		assertTrue(createPage.createSuccess());
	}

	@Test
	public void createExistingUsername() {
		createPage.goTo();
		createPage.createUserWithPassword("mrtest", "tom@somwehre.com", "Password1", "Password1");
		assertFalse(createPage.createSuccess());
	}

	@Test
	public void createExistingEmail() {
		createPage.goTo();
		createPage.createUserWithPassword("tom", "manager@nowhere.com", "Password1", "Password1");
		assertFalse(createPage.createSuccess());
	}

	@Test
	public void createNoPasswordMatch() {
		createPage.goTo();
		createPage.createUserWithPassword("tom", "manager@nowhere.com", "Password1", "Different1");
		assertFalse(createPage.createSuccess());
	}

	@Test
	public void testCreateUserWithoutPassword() {
		createPage.goTo();
		createPage.createUserWithoutPassword("tom", "tom@somwehre.com");
		assertTrue(createPage.createSuccess());
	}

}
