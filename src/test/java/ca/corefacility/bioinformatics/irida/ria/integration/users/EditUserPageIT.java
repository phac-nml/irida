package ca.corefacility.bioinformatics.irida.ria.integration.users;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.ria.integration.drivers.IridaPhantomJSDriver;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.LoginPage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.user.EditUserPage;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiJdbcDataSourceConfig.class,
		IridaApiPropertyPlaceholderConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/users/EditUserPageIT.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class EditUserPageIT {
	private WebDriver driver;
	private EditUserPage editPage;

	@Before
	public void setup() {
		driver = new IridaPhantomJSDriver();
		LoginPage.loginAsAdmin(driver);

		editPage = new EditUserPage(driver);
	}

	@After
	public void destroy() {
		if (driver != null) {
			driver.close();
			driver.quit();
		}
	}

	@Test
	public void testUpdateFirstName() {
		String newName = "newFirstName";
		String updateName = editPage.updateFirstName(newName);
		assertTrue(editPage.updateSuccess());
		assertTrue(updateName.contains(newName));
	}

	@Test
	public void testUpdatePassword() {
		String newPassword = "paSsW0Rd";
		editPage.updatePassword(newPassword, newPassword);
		assertTrue(editPage.updateSuccess());
	}

	@Test
	public void testUpdatePasswordFail() {
		String newPassword = "paSsW0Rd";
		editPage.updatePassword(newPassword, "notthesame");
		assertFalse(editPage.updateSuccess());
	}

}
