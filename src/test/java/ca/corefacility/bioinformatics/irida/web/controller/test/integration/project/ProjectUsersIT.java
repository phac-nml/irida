package ca.corefacility.bioinformatics.irida.web.controller.test.integration.project;

import static ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils.asUser;
import static io.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.LocalHostUriTemplateHandler;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.annotation.RestIntegrationTest;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.net.HttpHeaders;
import io.restassured.response.Response;

/**
 * Integration test for project and user.
 * 
 */
@RestIntegrationTest
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/web/controller/test/integration/project/ProjectUsersIntegrationTest.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class ProjectUsersIT {

	@Autowired
	private LocalHostUriTemplateHandler uriTemplateHandler;

	@Test
	public void testAddExistingUserToProject() {
		String username = "tom";
		String projectUri = uriTemplateHandler.getRootUri() + "/api/projects/1";
		Map<String, String> users = new HashMap<>();
		users.put("userId", username);

		// get the project
		String projectJson = asUser().get(projectUri).asString();
		// get the uri for adding users to the project
		String usersUri = from(projectJson).get("resource.links.find{it.rel == 'project/users'}.href");

		// post the users uri to add tom to the project
		Response r = asUser().given().body(users).expect().statusCode(HttpStatus.CREATED.value()).when().post(usersUri);

		// check that the locations make sense
		String location = r.getHeader(HttpHeaders.LOCATION);

		assertNotNull(location);
		assertEquals(projectUri + "/users/" + username, location);

		// confirm that tom is part of the project now
		asUser().expect().body("resource.resources.username", hasItem(username)).when().get(usersUri);
	}

	@Test
	public void testRemoveUserFromProject() {
		String name = "Josh";
		String projectUri = uriTemplateHandler.getRootUri() + "/api/projects/2";

		// get the project
		String projectJson = asUser().get(projectUri).asString();
		String projectUsersUri = from(projectJson).get("resource.links.find{it.rel=='project/users'}.href");
		String projectUsersJson = asUser().get(projectUsersUri).asString();
		String userRelationshipUri = from(projectUsersJson).get(
				"resource.resources.find{it.firstName == '" + name + "'}.links.find{it.rel == 'relationship'}.href");

		// delete the user relationship
		asUser().expect().body("resource.links.rel", hasItems("project", "project/users")).when()
				.delete(userRelationshipUri);

		// get the project again and confirm that josh isn't part of the project
		// anymore
		asUser().expect().body("resource.resources.firstName", not(hasItem(name))).when().get(projectUsersUri);
	}
}
