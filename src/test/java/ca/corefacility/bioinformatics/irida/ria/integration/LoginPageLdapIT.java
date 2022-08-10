package ca.corefacility.bioinformatics.irida.ria.integration;

import javax.naming.ldap.LdapName;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ldap.LdapAutoConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.ldap.test.EmbeddedLdapServer;
import org.springframework.ldap.test.unboundid.EmbeddedLdapServerFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;

import org.springframework.ldap.test.unboundid.LdapTestUtils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <p>
 * Integration test to ensure that the Login Page works and redirects the user to the dashboard.
 * </p>
 */
@ContextConfiguration(classes = LdapAutoConfiguration.class)
@TestPropertySource(
	locations = "/ca/corefacility/bioinformatics/irida/config/ldap/ldap.properties",
	properties = {
		"spring.ldap.embedded.ldif=/ca/corefacility/bioinformatics/irida/config/ldap/test_server.ldif",
		"spring.ldap.embedded.base-dn=o=phac",
		"spring.ldap.embedded.port=18880",
		"spring.ldap.embedded.url=ldap://localhost:18880/",
		"spring.ldap.embedded.credential.username=uid=admin,ou=system",
		"spring.ldap.embedded.credential.password=secret",
		"spring.ldap.embedded.validation.enabled=true",
}
)
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/LoginPageLdapIT.xml")
public class LoginPageLdapIT extends AbstractIridaUIITChromeDriver {

	private static final String EXPIRED_USERNAME = "expiredGuy";
	private static final String EXPIRED_PASSWORD = "Password1!";

//	@Autowired
//	private ContextSource contextSource;

	// Base DN for test data
	private static final LdapName baseName = LdapUtils.newLdapName("o=phac");

	private static final int ldapPort = 18880;

//	@Autowired
//	LdapTemplate ldapTemplate;

//	@Autowired
//	EmbeddedLdapServer embeddedLdapServer;

	@Test
	public void testGoodLogin() throws Exception {
		// Bind to the directory
//		LdapContextSource contextSource = new LdapContextSource();
//		contextSource.setUrl("ldap://127.0.0.1:18880");
//		contextSource.setUserDn("uid=admin,ou=system");
//		contextSource.setPassword("secret");
//		contextSource.setPooled(false);
//		contextSource.afterPropertiesSet();
////		// Create the Sprint LDAP template
//		LdapTemplate template = new LdapTemplate(contextSource);


//		LdapTestUtils.startEmbeddedServer(ldapPort,baseName.toString(),"iridaldap");
//		LdapTestUtils.cleanAndSetup(ldapTemplate.getContextSource(), baseName, null);
//		LdapTestUtils.cleanAndSetup(template.getContextSource(), baseName, new ClassPathResource("/ca/corefacility/bioinformatics/irida/config/ldap/test_server.ldif"));
//		LdapTestUtils.cleanAndSetup(contextSource, baseName, new FileSystemResource("/ca/corefacility/bioinformatics/irida/config/ldap/test_server.ldif"));

		LoginPage.login(driver(), LoginPage.JANE_USERNAME, LoginPage.JANE_PASSWORD);
		assertTrue(driver().getTitle().contains("Dashboard"), "The 'test' user is logged in and redirected.");
	}

//	@Test
//	public void testBadUsername() throws Exception {
//		LoginPage page = LoginPage.to(driver());
//		assertFalse(page.isLoginErrorDisplayed(), "No login errors should be originally displayed");
//		page.login(LoginPage.BAD_USERNAME, LoginPage.GOOD_PASSWORD);
//		assertTrue(driver().getCurrentUrl().contains("login?error=true"), "Should update the url with '?error=true'");
//		assertTrue(page.isLoginErrorDisplayed(), "Should display error on bad login");
//	}
//
//	@Test
//	public void testBadPassword() throws Exception {
//		LoginPage page = LoginPage.to(driver());
//		assertFalse(page.isLoginErrorDisplayed(), "No login errors should be originally displayed");
//		page.login(LoginPage.USER_USERNAME, LoginPage.BAD_PASSWORD);
//		assertTrue(driver().getCurrentUrl().contains("login?error=true"), "Should update the url with '?error=true'");
//		assertTrue(page.isLoginErrorDisplayed(), "Should display error on bad login");
//	}
//
//	@Test
//	public void testExpiredCredentialsLogin() throws Exception {
//		LoginPage page = LoginPage.to(driver());
//		page.login(EXPIRED_USERNAME, EXPIRED_PASSWORD);
//		assertTrue(driver().getCurrentUrl().contains("password_reset/"),
//				"The 'expiredGuy' user should be sent to a password reset page.");
//	}
//
//	@Test
//	public void testLoginWithChangedCredentials() {
//		String newPassword = "aGoodP@ssW0rD";
//		LoginPage page = LoginPage.to(driver());
//		page.login(EXPIRED_USERNAME, EXPIRED_PASSWORD);
//		PasswordResetPage passwordResetPage = new PasswordResetPage(driver());
//		passwordResetPage.enterPassword(newPassword);
//		passwordResetPage.clickSubmit();
//		assertTrue(passwordResetPage.checkSuccess(), "Should have succeeded in changing password.");
//
//		AbstractPage.logout(driver());
//		page = LoginPage.to(driver());
//		page.login(EXPIRED_USERNAME, newPassword);
//		assertTrue(driver().getTitle().contains("Dashboard"), "The user is logged in and redirected.");
//	}
//
//	@Test
//	public void testSequencerLogin() throws Exception {
//		LoginPage page = LoginPage.to(driver());
//		assertFalse(page.isLoginErrorDisplayed(), "No login errors should be originally displayed");
//		page.login(LoginPage.SEQUENCER_USERNAME, LoginPage.GOOD_PASSWORD);
//		assertFalse(driver().getTitle().contains("Dashboard"),
//				"The sequencer user should not be able to see the dashboard");
//		assertTrue(driver().getCurrentUrl().contains("login?error=true&sequencer-login=true"),
//				"Should update the url with '?error=true&sequencer-login=true'");
//		assertTrue(page.isLoginErrorDisplayed(), "Should display error on bad login");
//	}
}
