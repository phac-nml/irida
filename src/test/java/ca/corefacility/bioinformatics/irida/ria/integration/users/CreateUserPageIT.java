package ca.corefacility.bioinformatics.irida.ria.integration.users;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.user.CreateUserPage;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiJdbcDataSourceConfig.class,
		IridaApiPropertyPlaceholderConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/users/CreateUserPageIT.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class CreateUserPageIT {
	private WebDriver driver;
	private CreateUserPage createPage;
	LoginPage loginPage;

	@Before
	public void setup() {
		driver = new PhantomJSDriver();
		loginPage = LoginPage.to(driver);
		loginPage.doLogin();

		createPage = new CreateUserPage(driver);
	}

	@After
	public void destroy() {
		if (driver != null) {
			driver.close();
			driver.quit();
		}
	}

	@Test
	public void createGoodUser() {
		createPage.createUserWithPassword("tom", "tom@somwehre.com", "Password1", "Password1");
		assertTrue(createPage.createSuccess());
	}

	@Test
	public void createExistingUsername() {
		createPage.createUserWithPassword("mrtest", "tom@somwehre.com", "Password1", "Password1");
		assertFalse(createPage.createSuccess());
	}

	@Test
	public void createExistingEmail() {
		createPage.createUserWithPassword("tom", "manager@nowhere.com", "Password1", "Password1");
		assertFalse(createPage.createSuccess());
	}

	@Test
	public void createNoPasswordMatch() {
		createPage.createUserWithPassword("tom", "manager@nowhere.com", "Password1", "Different1");
		assertFalse(createPage.createSuccess());
	}

	@Test
	public void testCreateUserWithoutPassword() {
		createPage.createUserWithoutPassword("tom", "tom@somwehre.com");
		assertTrue(createPage.createSuccess());
	}

}
