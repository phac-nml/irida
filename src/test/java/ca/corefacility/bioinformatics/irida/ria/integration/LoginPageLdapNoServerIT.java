package ca.corefacility.bioinformatics.irida.ria.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.ldap.LdapAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 * Integration test to verify error thrown when no ldap server is started but user attempts ldap login.
 * </p>
 */
@ContextConfiguration(classes = LdapAutoConfiguration.class)
@TestPropertySource(locations = "/ca/corefacility/bioinformatics/irida/config/ldap/ldap.properties")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/LoginPageLdapIT.xml")
public class LoginPageLdapNoServerIT extends AbstractIridaUIITChromeDriver {

	private static final String JOHN_USERNAME = "jwick";
	private static final String JOHN_PASSWORD = "ldappassword";

	private static final String MRTEST_USERNAME = "mrtest";
	private static final String MRTEST_PASSWORD = "Password1!";

	/**
	 * Test signing in with a ldap user when there is no LDAP server
	 *
	 * @throws Exception
	 */
	@Test
	public void testLoginNoLdapServer() throws Exception {
		LoginPage page = LoginPage.to(driver());
		assertFalse(page.isLoginErrorDisplayed(), "No login errors should be originally displayed");
		page.login(JOHN_USERNAME, JOHN_PASSWORD);
		String expectedUrl = driver().getCurrentUrl().substring(0, 23) + "irida/login?ldap-error=6";
		assertEquals(expectedUrl, driver().getCurrentUrl());
		assertTrue(page.isLoginErrorDisplayed(), "Should display error on bad login");
	}

	/**
	 * Test signing in with a local db only user when there is no LDAP server
	 *
	 * @throws Exception
	 */
	@Test
	public void testLoginLocalNoLdapServer() throws Exception {
		LoginPage.login(driver(), MRTEST_USERNAME, MRTEST_PASSWORD);
		assertTrue(driver().getTitle().contains("Dashboard"), "The 'mrtest' user is logged in and redirected.");
	}
}

