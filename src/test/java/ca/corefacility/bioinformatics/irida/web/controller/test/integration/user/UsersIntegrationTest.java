package ca.corefacility.bioinformatics.irida.web.controller.test.integration.user;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;

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
		Map<String, String> user = new HashMap<>();
		user.put("username", "franklin");
		user.put("password", "Password1");
		user.put("email", "fbristow@phac-aspc.gc.ca");
		user.put("firstName", "Franklin");
		user.put("lastName", "Bristow");
		user.put("phoneNumber", "7029");
		// doesn't matter what the user is, we should fail here when trying to
		// create a user because the current user doesn't have permission to
		// create users.
		given().body(user).expect().response().statusCode(HttpStatus.SC_FORBIDDEN).when().post("/users");
	}
}
