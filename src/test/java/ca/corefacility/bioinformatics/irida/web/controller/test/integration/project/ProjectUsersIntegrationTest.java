package ca.corefacility.bioinformatics.irida.web.controller.test.integration.project;

import com.google.common.net.HttpHeaders;
import com.jayway.restassured.response.Response;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static com.jayway.restassured.RestAssured.*;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Integration test for project and user.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class ProjectUsersIntegrationTest {
	@Test
	public void testAddExistingUserToProject() {
		String username = "tom";
		String projectUri = "http://localhost:8080/projects/100";
		Map<String, String> users = new HashMap<>();
		users.put("userId", username);

		// get the project
		String projectJson = get(projectUri).asString();
		// get the uri for adding users to the project
		String usersUri = from(projectJson).get("resource.links.find{it.rel == 'project/users'}.href");

		// post the users uri to add tom to the project
		Response r = given().body(users).expect().statusCode(HttpStatus.CREATED.value()).when().post(usersUri);

		// check that the locations make sense
		String location = r.getHeader(HttpHeaders.LOCATION);

		assertNotNull(location);
		assertEquals(projectUri + "/users/" + username, location);

		// confirm that tom is part of the project now
		expect().body("resource.resources.username", hasItem(username)).when().get(usersUri);
	}

	@Test
	public void testRemoveUserFromProject() {
		String name = "Josh";
		String projectUri = "http://localhost:8080/projects/2";

		// get the project
		String projectJson = get(projectUri).asString();
		String projectUsersUri = from(projectJson).get("resource.links.find{it.rel=='project/users'}.href");
		String projectUsersJson = get(projectUsersUri).asString();
		String userRelationshipUri = from(projectUsersJson).get(
				"resource.resources.find{it.firstName == '" + name + "'}.links.find{it.rel == 'relationship'}.href");

		// delete the user relationship
		expect().body("resource.links.rel", hasItems("project", "project/users")).when().delete(userRelationshipUri);

		// get the project again and confirm that josh isn't part of the project
		// anymore
		expect().body("resource.resources.firstName", not(hasItem(name))).when().get(projectUsersUri);
	}
}
