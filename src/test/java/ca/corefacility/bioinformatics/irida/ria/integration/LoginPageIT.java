package ca.corefacility.bioinformatics.irida.ria.integration;

import ca.corefacility.bioinformatics.irida.config.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.net.MalformedURLException;

import static org.junit.Assert.assertEquals;

/**
 * <p>
 * Integration test to ensure that the Login Page works and redirects the user
 * to the dashboard.
 * </p>
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiJdbcDataSourceConfig.class,
		IridaApiPropertyPlaceholderConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@ActiveProfiles("dev")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/LoginPageIT.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class LoginPageIT {

	private LoginPage loginPage;
	private WebDriver driver;

	@Before
	public void setup() throws MalformedURLException {
		driver = new ChromeDriver();
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
	public void testGoodLogin() throws Exception {
		loginPage.login(LoginPage.GOOD_USERNAME, LoginPage.GOOD_PASSWORD);
		assertEquals("The 'test' user is logged in and redirected.", "http://localhost:8080/dashboard",
				driver.getCurrentUrl());
	}
}
