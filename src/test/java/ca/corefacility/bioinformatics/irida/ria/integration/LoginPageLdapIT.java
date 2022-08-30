package ca.corefacility.bioinformatics.irida.ria.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ldap.LdapAutoConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 * Integration test to ensure that the Login Page works and redirects the user to the dashboard.
 * </p>
 */
@ContextConfiguration(classes = LdapAutoConfiguration.class)
@TestPropertySource(
	locations = "/ca/corefacility/bioinformatics/irida/config/ldap/ldap.properties",
	properties = {
		"spring.ldap.embedded.ldif=classpath:ca/corefacility/bioinformatics/irida/config/ldap/test_server.ldif",
		"spring.ldap.embedded.base-dn=dc=springframework,dc=org",
		"spring.ldap.embedded.port=18880",
		"spring.ldap.embedded.url=ldap://localhost:18880/",
		"spring.ldap.embedded.credential.username=uid=admin",
		"spring.ldap.embedded.credential.password=secret",
		"spring.ldap.embedded.validation.enabled=false",
})
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/LoginPageLdapIT.xml")
public class LoginPageLdapIT extends AbstractIridaUIITChromeDriver {

	private static final String JOHN_USERNAME = "jwick";
	private static final String JOHN_PASSWORD = "Password1!";

	private static final String TORONTO_USERNAME = "ttokyo";
	private static final String TORONTO_PASSWORD = "Password1!";

	private static final String MIRA_USERNAME = "mira";
	private static final String MIRA_PASSWORD = "Password1!";

	private static final String COLLAPSE_USERNAME = "collapse";
	private static final String COLLAPSE_PASSWORD = "Password1!";

	private static final String YATORO_USERNAME = "yatoro";
	private static final String YATORO_PASSWORD = "Password1!";

	private static final String MRTEST_USERNAME = "mrtest";
	private static final String MRTEST_PASSWORD = "Password1!";

	@Autowired
	UserRepository userRepository;

	/**
	 * Test signing in with user that exists in ldap, and in database
	 * @throws Exception
	 */
	@Test
	public void testGoodLogin() throws Exception {
		LoginPage.login(driver(), JOHN_USERNAME, JOHN_PASSWORD);
		assertTrue(driver().getTitle().contains("Dashboard"), "The 'jwick' user is logged in and redirected.");
	}

	/**
	 * Test signing in with user that does not exist in ldap
	 * @throws Exception
	 */
	@Test
	public void testBadUsername() throws Exception {
		LoginPage page = LoginPage.to(driver());
		assertFalse(page.isLoginErrorDisplayed(), "No login errors should be originally displayed");
		page.login(LoginPage.BAD_USERNAME, LoginPage.GOOD_PASSWORD);
		assertTrue(driver().getCurrentUrl().contains("login?error=true"), "Should update the url with '?error=true'");
		assertTrue(page.isLoginErrorDisplayed(), "Should display error on bad login");
	}

	/**
	 * Test signing in with user that does not exist in ldap but does exist in local db
	 * @throws Exception
	 */
	@Test
	public void testNotInLdap() throws Exception {
		// User exists in db
		UserDetails u = userRepository.loadUserByUsername(MRTEST_USERNAME);
		assertEquals(MRTEST_USERNAME, u.getUsername());
		// try sign in
		LoginPage page = LoginPage.to(driver());
		assertFalse(page.isLoginErrorDisplayed(), "No login errors should be originally displayed");
		page.login(MRTEST_USERNAME, MRTEST_PASSWORD);
		assertTrue(driver().getCurrentUrl().contains("login?error=true"), "Should update the url with '?error=true'");
		assertTrue(page.isLoginErrorDisplayed(), "Should display error on bad login");
	}

	/**
	 * Test signing in with the wrong password for a user that exists in ldap and in database
	 * @throws Exception
	 */
	@Test
	public void testBadPassword() throws Exception {
		LoginPage page = LoginPage.to(driver());
		assertFalse(page.isLoginErrorDisplayed(), "No login errors should be originally displayed");
		page.login(JOHN_USERNAME, LoginPage.BAD_PASSWORD);
		assertTrue(driver().getCurrentUrl().contains("login?error=true"), "Should update the url with '?error=true'");
		assertTrue(page.isLoginErrorDisplayed(), "Should display error on bad login");
	}

	/**
	 * Test signing in with user that exists in ldap, but not in database
	 * @throws Exception
	 */
	@Test
	public void testCreateAccountLogin() throws Exception {
		// User should not exist in db
		UsernameNotFoundException e = assertThrows(UsernameNotFoundException.class,
				() -> {userRepository.loadUserByUsername(TORONTO_USERNAME);});
		LoginPage.login(driver(), TORONTO_USERNAME, TORONTO_PASSWORD);
		assertTrue(driver().getTitle().contains("Dashboard"), "The 'ttokyo' user is logged in and redirected.");
		// User should now exist in db
		UserDetails u = userRepository.loadUserByUsername(TORONTO_USERNAME);
		assertEquals(TORONTO_USERNAME, u.getUsername());
	}

	/**
	 * Test signing in with user that exists in ldap, not in database, and missing required field in ldap
	 * @throws Exception
	 */
	@Test
	public void testCreateAccountMissingFields() throws Exception {
		// User should not exist in db
		UsernameNotFoundException e = assertThrows(UsernameNotFoundException.class,
				() -> {userRepository.loadUserByUsername(MIRA_USERNAME);});
		LoginPage page = LoginPage.to(driver());
		assertFalse(page.isLoginErrorDisplayed(), "No login errors should be originally displayed");
		page.login(MIRA_USERNAME, MIRA_PASSWORD);
		assertTrue(driver().getCurrentUrl().contains("login?ldap-error=4"), "Should update the url with '?ldap-error=4'");
		assertTrue(page.isLoginErrorDisplayed(), "Should display error on bad login");
	}

	/**
	 * Test signing in with user that has updated fields in ldap server
	 * @throws Exception
	 */
	@Test
	public void testUpdateAccountLogin() throws Exception {
		// User should have old email in db
		User u = userRepository.loadUserByUsername(YATORO_USERNAME);
		assertEquals("yatoro_old_email@example.com", u.getEmail());
		// Sign in
		LoginPage.login(driver(), YATORO_USERNAME, YATORO_PASSWORD);
		assertTrue(driver().getTitle().contains("Dashboard"), "The 'yatoro' user is logged in and redirected.");
		// User should now have email updated in db
		u = userRepository.loadUserByUsername(YATORO_USERNAME);
		assertEquals("yatoro@example.com", u.getEmail());
	}

	/**
	 * Test signing in with user that exists in ldap, not in database, and fields have bad data
	 * @throws Exception
	 */
	@Test
	public void testCreateAccountInvalidFields() throws Exception {
		// User should not exist in db
		UsernameNotFoundException e = assertThrows(UsernameNotFoundException.class,
				() -> {userRepository.loadUserByUsername(COLLAPSE_USERNAME);});
		LoginPage page = LoginPage.to(driver());
		assertFalse(page.isLoginErrorDisplayed(), "No login errors should be originally displayed");
		page.login(COLLAPSE_USERNAME, COLLAPSE_PASSWORD);
		assertTrue(driver().getCurrentUrl().contains("login?ldap-error=2"), "Should update the url with '?ldap-error=2'");
		assertTrue(page.isLoginErrorDisplayed(), "Should display error on bad login");
	}

	/**
	 * Test signing in with sequencer account
	 * TODO: borked
	 * @throws Exception
	 */
	@Test
	public void testSequencerLogin() throws Exception {
		LoginPage page = LoginPage.to(driver());
		assertFalse(page.isLoginErrorDisplayed(), "No login errors should be originally displayed");
		page.login(LoginPage.SEQUENCER_USERNAME, LoginPage.GOOD_PASSWORD);
		assertFalse(driver().getTitle().contains("Dashboard"),
				"The sequencer user should not be able to see the dashboard");
		assertTrue(driver().getCurrentUrl().contains("login?error=true&sequencer-login=true"),
				"Should update the url with '?error=true&sequencer-login=true'");
		assertTrue(page.isLoginErrorDisplayed(), "Should display error on bad login");
	}
}