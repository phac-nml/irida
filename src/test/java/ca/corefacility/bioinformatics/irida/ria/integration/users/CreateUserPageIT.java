package ca.corefacility.bioinformatics.irida.ria.integration.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIITChromeDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.user.CreateUserPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/users/CreateUserPageIT.xml")
public class CreateUserPageIT extends AbstractIridaUIITChromeDriver {
	private CreateUserPage createPage;

	@BeforeEach
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
		assertTrue(createPage.createSuccess("User account: tom"));
	}

	@Test
	public void createExistingUsername() {
		createPage.goTo();
		createPage.enterUserCredsWithPassword("mrtest", "tom@somwehre.com", "Password1!", "Password1!");
		createPage.waitForJQueryAjaxResponse();
		assertTrue(createPage.hasErrors());
		createPage.clickSubmit();
		assertFalse(createPage.createSuccess("User account: mrtest"));
	}

	@Test
	public void createExistingEmail() {
		createPage.goTo();
		createPage.enterUserCredsWithPassword("tom", "manager@nowhere.com", "Password1!", "Password1!");
		createPage.waitForJQueryAjaxResponse();
		assertTrue(createPage.hasErrors());
		createPage.clickSubmit();
		assertFalse(createPage.createSuccess("User account: tom"));
	}

	@Test
	public void createNoPasswordMatch() {
		createPage.goTo();
		createPage.enterUserCredsWithPassword("tom", "manager@nowhere.com", "Password1!", "Different1");
		createPage.waitForJQueryAjaxResponse();
		assertTrue(createPage.hasErrors());
		assertTrue(createPage.isSubmitEnabled());
		createPage.clickSubmit();
		assertFalse(createPage.createSuccess("User account: tom"));
	}

	@Test
	public void testCreateUserWithoutPassword() {
		createPage.goTo();
		createPage.enterUserCredsWithoutPassword("tom", "tom@somwehre.com");
		createPage.waitForJQueryAjaxResponse();
		assertFalse(createPage.hasErrors());
		createPage.clickSubmit();
		assertTrue(createPage.createSuccess("User account: tom"));
	}

}
