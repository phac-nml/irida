package ca.corefacility.bioinformatics.irida.web.controller.test.integration.user;

import static ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils.asAdmin;
import static ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils.asManager;
import static ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils.asRole;
import static ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils.asUser;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

/**
 * Integration tests for users.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiJdbcDataSourceConfig.class,
		IridaApiPropertyPlaceholderConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/web/controller/test/integration/user/UserIntegrationTest.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class UsersIntegrationTest {

	@Test
	public void testBadUsernameLogin() {
		asRole(ITestAuthUtils.BAD_USERNAME).expect().statusCode(HttpStatus.SC_UNAUTHORIZED).when().get("/");
	}

	@Test
	public void testBadPasswordLogin() {
		asRole(ITestAuthUtils.BAD_PASSWORD).expect().statusCode(HttpStatus.SC_UNAUTHORIZED).when().get("/");
	}

	@Test
	public void testGetAllUsers() {
		asAdmin().expect().body("resource.links.rel", hasItems("self")).and()
				.body("resource.resources.username", hasItem("fbristow")).when().get("/users");
	}

	@Test
	public void testGetCurrentUser() {
		asUser().expect().body("resource.links.rel", hasItems("self", "user/projects")).and()
				.body("resource.username", is("fbristow")).when().get("/users/current");
	}

	@Test
	public void testCreateUserFail() {
		// doesn't matter what the user is, we should fail here when trying to
		// create a user because the current user doesn't have permission to
		// create users.
		asUser().given().body(createUser()).expect().response().statusCode(HttpStatus.SC_FORBIDDEN).when()
				.post("/users");
	}

	@Test
	public void testCreateUserAsAdminSucceed() {
		Map<String, String> user = createUser();

		asAdmin().given().body(user).expect().response().statusCode(HttpStatus.SC_CREATED).when().post("/users");
	}

	@Test
	public void testCreateUserAsManagerSucceed() {
		Map<String, String> user = createUser();
		asManager().given().body(user).expect().response().statusCode(HttpStatus.SC_CREATED).when().post("/users");
	}

	@Test
	public void testUpdateOtherAccountFail() {
		asUser().given().body(createUser()).expect().response().statusCode(HttpStatus.SC_FORBIDDEN).when()
				.patch("/users/2");
	}

	@Test
	public void testUpdateOwnAccountSucceed() {
		// figure out what the uri is for the current user
		String responseBody = asUser().get("/users/current").asString();
		String location = from(responseBody).getString("resource.links.find{it.rel == 'self'}.href");
		Map<String, String> user = new HashMap<>();
		String phoneNumber = "867-5309";
		user.put("phoneNumber", phoneNumber);
		asUser().given().body(user).expect().response().statusCode(HttpStatus.SC_OK).when().patch(location);
		asUser().expect().body("resource.phoneNumber", is(phoneNumber)).when().get("/users/current");
	}

	private Map<String, String> createUser() {
		String username = RandomStringUtils.randomAlphanumeric(20);
		String email = RandomStringUtils.randomAlphanumeric(20) + "@" + RandomStringUtils.randomAlphanumeric(5) + ".ca";
		Map<String, String> user = new HashMap<>();
		user.put("username", username);
		user.put("password", "Password1");
		user.put("email", email);
		user.put("firstName", "Franklin");
		user.put("lastName", "Bristow");
		user.put("phoneNumber", "7029");
		return user;
	}
}
