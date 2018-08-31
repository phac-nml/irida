package ca.corefacility.bioinformatics.irida.web.controller.test.integration.project;

import static ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils.asUser;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestSystemProperties;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.net.HttpHeaders;
import com.jayway.restassured.response.Response;

/**
 * Integration test for project and user.
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiJdbcDataSourceConfig.class,
		IridaApiPropertyPlaceholderConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/web/controller/test/integration/project/ProjectUsersIntegrationTest.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class ProjectUsersIT {
	@Test
	public void testAddExistingUserToProject() {
		String username = "tom";
		String projectUri = ITestSystemProperties.BASE_URL + "/api/projects/1";
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
		String projectUri = ITestSystemProperties.BASE_URL + "/api/projects/2";

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
