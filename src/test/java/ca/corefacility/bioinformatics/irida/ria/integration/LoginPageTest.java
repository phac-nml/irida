package ca.corefacility.bioinformatics.irida.ria.integration;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;

/**
 * <p>
 * Integration test to ensure that the Login Page works and redirects the user
 * to the dashboard.
 * </p>
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class LoginPageTest {
	@Autowired
	private WebApplicationContext context;

	private LoginPage loginPage;
	private WebDriver driver;

	@Before
	public void setup() {
		driver = new HtmlUnitDriver(true);
		loginPage = LoginPage.to(driver);
	}

	@After
	public void destroy() {
		if (driver != null) {
			driver.close();
		}
	}

	@Test
	public void testBadUsername() throws Exception {
		loginPage.login(LoginPage.BAD_USERNAME, LoginPage.GOOD_PASSWORD);
		assertEquals("Should update the url with '?error=true'", driver.getCurrentUrl(),
				"http://localhost:8080/login?error=true");
		assertEquals("Should display error on bad login", loginPage.getError(), "Incorrect Email or Password");
	}

	@Test
	public void testBadPassword() throws Exception {
		loginPage.login(LoginPage.GOOD_USERNAME, LoginPage.BAD_PASSWORD);
		assertEquals("Should update the url with '?error=true'", driver.getCurrentUrl(),
				"http://localhost:8080/login?error=true");
		assertEquals("Should display error on bad login", loginPage.getError(), "Incorrect Email or Password");
	}

	@Test
	public void testGoodLogout() throws Exception {
		loginPage.login(LoginPage.GOOD_USERNAME, LoginPage.GOOD_PASSWORD);
		assertEquals("The 'test' user is logged in and redirected.", driver.getCurrentUrl(),
				"http://localhost:8080/app");
	}
}
