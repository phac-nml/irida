package ca.corefacility.bioinformatics.irida.ria.integration.users;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

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
import ca.corefacility.bioinformatics.irida.ria.integration.pages.user.UserDetailsPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.TestUtilities;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiJdbcDataSourceConfig.class,
		IridaApiPropertyPlaceholderConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/users/UserDetailsPageIT.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class UserDetailsPageIT {
	private static WebDriver driver;
	private UserDetailsPage usersPage;

	@BeforeClass
	public static void setup() {
		driver = TestUtilities.setDriverDefaults(new PhantomJSDriver());
	}

	@Before
	public void setUpTest() {
		LoginPage.loginAsManager(driver);
		usersPage = new UserDetailsPage(driver);
	}

	@After
	public void tearDown() {
		LoginPage.logout(driver);
	}

	@AfterClass
	public static void destroy() {
		driver.quit();
	}

	@Test
	public void testGetUserId() {
		usersPage.getCurrentUser();
		String currentUserId = usersPage.getUserId();
		assertEquals(String.valueOf(1L), currentUserId);
	}

	@Test
	public void testGetOtherUserId() {
		Long id = 2L;
		usersPage.getOtherUser(id);
		String otherUserId = usersPage.getUserId();
		assertEquals(String.valueOf(id), otherUserId);
	}

	@Test
	public void testGetEditUserButton() {
		assertTrue("Should see button for user 1", usersPage.canGetEditLink(1L));
		assertFalse("Should not see button for user 2", usersPage.canGetEditLink(2L));
	}

	@Test
	public void testGetUserProjects() {
		usersPage.getOtherUser(1L);
		List<String> userProjectIds = usersPage.getUserProjectIds();
		assertTrue(userProjectIds.contains("1"));
		assertTrue(userProjectIds.contains("2"));
	}

	@Test
	public void testResetUserPassword() {
		usersPage.getOtherUser(1L);
		usersPage.sendPasswordReset();
		assertTrue(usersPage.checkSuccessNotification());
	}

}
