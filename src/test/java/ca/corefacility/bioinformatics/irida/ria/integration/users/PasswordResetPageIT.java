package ca.corefacility.bioinformatics.irida.ria.integration.users;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ca.corefacility.bioinformatics.irida.ria.integration.AbstractIridaUIIT;
import org.junit.*;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.user.PasswordResetPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.TestUtilities;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/PasswordResetPageIT.xml")
public class PasswordResetPageIT extends AbstractIridaUIIT {
	private static final String RESET_USER = "differentUser";

	private PasswordResetPage passwordResetPage;

	@Before
	public void setUpTest() {
		// Don't do login here! should be able to go through this without
		// logging in

		passwordResetPage = new PasswordResetPage(driver());
	}

	@After
	@Override
	public void tearDown() {
		// don't log out, we didn't log in!
	}

	@Test
	public void testSetPassword() {
		String password = "Password1";
		passwordResetPage.getPasswordReset("XYZ");
		passwordResetPage.enterPassword(password, password);
		assertTrue(passwordResetPage.checkSuccess());
	}

	@Test
	public void testSetUnequalPassword() {
		String password = "Password1";
		passwordResetPage.getPasswordReset("XYZ");
		passwordResetPage.enterPassword(password, "different1");
		assertFalse(passwordResetPage.checkSuccess());
	}

	@Test
	public void testSetBadPassword() {
		String password = "notcomplex";
		passwordResetPage.getPasswordReset("XYZ");
		passwordResetPage.enterPassword(password, password);
		assertFalse(passwordResetPage.checkSuccess());
	}

	@Test
	public void testChangedCredentials() {
		String password = "Password1";
		// reset password
		passwordResetPage.getPasswordReset("XYZ");
		passwordResetPage.enterPassword(password, password);
		assertTrue(passwordResetPage.checkSuccess());

		passwordResetPage.logout(driver());
		// try new password
		LoginPage.login(driver(), RESET_USER, password);
		assertTrue("The user is logged in and redirected.", driver().getCurrentUrl().contains("dashboard"));
		LoginPage.logout(driver());
	}
}
