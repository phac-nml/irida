package ca.corefacility.bioinformatics.irida.ria.integration.users;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.user.CreateUserPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/users/CreateUserPageIT.xml")
public class CreateUserPageIT extends AbstractIridaUIITChromeDriver {
	private CreateUserPage createPage;

	@Before
	public void setUpTest() {
		LoginPage.loginAsManager(driver());
		createPage = new CreateUserPage(driver());
	}

	@Test
	public void createGoodUser() {
		createPage.goTo();
		createPage.enterUserCredsWithPassword("tom", "tom@somwehre.com", "Password1!", "Password1!");
		createPage.waitForJQueryAjaxResponse();
		assertFalse(createPage.hasErrors());
		createPage.clickSubmit();
		assertTrue(createPage.createSuccess());
	}

	@Test
	public void createExistingUsername() {
		createPage.goTo();
		createPage.enterUserCredsWithPassword("mrtest", "tom@somwehre.com", "Password1!", "Password1!");
		createPage.waitForJQueryAjaxResponse();
		assertTrue(createPage.hasErrors());
		createPage.clickSubmit();
		assertFalse(createPage.createSuccess());
	}

	@Test
	public void createExistingEmail() {
		createPage.goTo();
		createPage.enterUserCredsWithPassword("tom", "manager@nowhere.com", "Password1!", "Password1!");
		createPage.waitForJQueryAjaxResponse();
		assertTrue(createPage.hasErrors());
		createPage.clickSubmit();
		assertFalse(createPage.createSuccess());
	}

	@Test
	public void createNoPasswordMatch() {
		createPage.goTo();
		createPage.enterUserCredsWithPassword("tom", "manager@nowhere.com", "Password1!", "Different1");
		createPage.waitForJQueryAjaxResponse();
		assertTrue(createPage.hasErrors());
		assertTrue(createPage.isSubmitEnabled());
		createPage.clickSubmit();
		assertFalse(createPage.createSuccess());
	}

	@Test
	public void testCreateUserWithoutPassword() {
		createPage.goTo();
		createPage.enterUserCredsWithoutPassword("tom", "tom@somwehre.com");
		createPage.waitForJQueryAjaxResponse();
		assertFalse(createPage.hasErrors());
		createPage.clickSubmit();
		assertTrue(createPage.createSuccess());
	}

}
