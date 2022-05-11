package ca.corefacility.bioinformatics.irida.web.controller.test.integration.user;

import static ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils.asAdmin;
import static ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils.asManager;
import static ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils.asRole;
import static ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils.asUser;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;

import java.util.HashMap;
import java.util.Map;

import io.restassured.http.ContentType;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.annotation.RestIntegrationTest;
import ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

/**
 * Integration tests for users.
 * 
 */
@RestIntegrationTest
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/web/controller/test/integration/user/UserIntegrationTest.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class UsersIT {

	@Test
	public void testBadUsernameLogin() {
		asRole(ITestAuthUtils.BAD_USERNAME).expect().statusCode(HttpStatus.SC_UNAUTHORIZED).when().get("/api");
	}

	@Test
	public void testBadPasswordLogin() {
		asRole(ITestAuthUtils.BAD_PASSWORD).expect().statusCode(HttpStatus.SC_UNAUTHORIZED).when().get("/api");
	}

	@Test
	public void testGetAllUsers() {
		asAdmin().expect().body("resource.links.rel", hasItems("self")).and()
				.body("resource.resources.username", hasItem("fbristow")).when().get("/api/users");
	}

	@Test
	public void testCreateUserFail() {
		// doesn't matter what the user is, we should fail here when trying to
		// create a user because the current user doesn't have permission to
		// create users.
		asUser().given().body(createUser()).contentType(ContentType.JSON).expect().response()
				.statusCode(HttpStatus.SC_FORBIDDEN).when().post("/api/users");
	}

	@Test
	public void testCreateUserAsAdminSucceed() {
		Map<String, String> user = createUser();

		asAdmin().given().body(user).contentType(ContentType.JSON).expect().response().statusCode(HttpStatus.SC_CREATED)
				.when().post("/api/users");
	}

	@Test
	public void testCreateUserAsManagerSucceed() {
		Map<String, String> user = createUser();
		asManager().given().body(user).contentType(ContentType.JSON).expect().response()
				.statusCode(HttpStatus.SC_CREATED).when().post("/api/users");
	}

	@Test
	public void testUpdateOtherAccountFail() {
		asUser().given().body(createUser()).contentType(ContentType.JSON).expect().response()
				.statusCode(HttpStatus.SC_FORBIDDEN).when().patch("/api/users/2");
	}

	private Map<String, String> createUser() {
		String username = RandomStringUtils.randomAlphanumeric(20);
		String email = RandomStringUtils.randomAlphanumeric(20) + "@" + RandomStringUtils.randomAlphanumeric(5) + ".ca";
		Map<String, String> user = new HashMap<>();
		user.put("username", username);
		user.put("password", "Password1!");
		user.put("email", email);
		user.put("firstName", "Franklin");
		user.put("lastName", "Bristow");
		user.put("phoneNumber", "7029");
		return user;
	}
}
