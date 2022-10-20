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
	private static final String JOHN_PASSWORD = "Password1!";

	/**
	 * Test signing in with user when there is no LDAP server
	 *
	 * @throws Exception
	 */
	@Test
	public void testLoginNoLdapServer() throws Exception {
		LoginPage page = LoginPage.to(driver());
		assertFalse(page.isLoginErrorDisplayed(), "No login errors should be originally displayed");
		page.login(JOHN_USERNAME, JOHN_PASSWORD);
		assertTrue(driver().getCurrentUrl().contains("login?ldap-error=6"), "Should update the url with '?ldap-error=6'");
		assertTrue(page.isLoginErrorDisplayed(), "Should display error on bad login");
	}
}

