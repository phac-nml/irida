package ca.corefacility.bioinformatics.irida.web.controller.test.integration.user;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.junit.Test;

/**
 * Integration tests for users.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class UsersIntegrationTest {

	@Test
	public void testGetAllUsers() {
		expect().body("resource.links.rel", hasItems("self", "collection/pages/first")).and()
				.body("resource.resources.username", hasItem("fbristow")).when().get("/users/all");
	}

	@Test
	public void testGetCurrentUser() {
		expect().body("resource.links.rel", hasItems("self", "user/projects")).and()
				.body("resource.username", is("fbristow")).when().get("/users/current");
	}

	@Test
	public void testCreateUserFail() {
		// doesn't matter what the user is, we should fail here when trying to
		// create a user because the current user doesn't have permission to
		// create users.
		given().body(createUser()).expect().response().statusCode(HttpStatus.SC_FORBIDDEN).when().post("/users");
	}

	@Test
	public void testCreateUserSucceed() {
		Map<String, String> user = createUser();
		System.out.println(user);
		given().body(user).and().auth().basic("admin", "password1").expect().response()
				.statusCode(HttpStatus.SC_CREATED).when().post("/users");
	}

	@Test
	public void testUpdateOtherAccountFail() {
		given().body(createUser()).expect().response().statusCode(HttpStatus.SC_FORBIDDEN).when().patch("/users/2");
	}

	@Test
	public void testUpdateOwnAccountSucceed() {
		// figure out what the uri is for the current user
		String responseBody = get("/users/current").asString();
		String location = from(responseBody).getString("resource.links.find{it.rel == 'self'}.href");
		Map<String, String> user = new HashMap<>();
		String phoneNumber = "867-5309";
		user.put("phoneNumber", phoneNumber);
		given().body(user).expect().response().statusCode(HttpStatus.SC_OK).when().patch(location);
		expect().body("resource.phoneNumber", is(phoneNumber)).when().get("/users/current");
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
