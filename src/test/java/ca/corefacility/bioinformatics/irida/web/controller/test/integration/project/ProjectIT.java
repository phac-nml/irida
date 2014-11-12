package ca.corefacility.bioinformatics.irida.web.controller.test.integration.project;

import static ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils.asAdmin;
import static ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils.asUser;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import com.jayway.restassured.response.Response;

/**
 * Integration tests for projects.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiJdbcDataSourceConfig.class,
		IridaApiPropertyPlaceholderConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/web/controller/test/integration/project/ProjectIntegrationTest.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class ProjectIT {

	private static final String PROJECTS = "/projects";

	/**
	 * If I try to issue a create request for an object with an invalid field
	 * name, the server should respond with 400.
	 */
	@Test
	public void testCreateProjectBadFieldName() {
		Response r = asUser().body("{ \"projectName\": \"some stupid project\" }").expect().response()
				.statusCode(HttpStatus.BAD_REQUEST.value()).when().post(PROJECTS);
		assertTrue(r.getBody().asString().contains("Unrecognized property"));
	}

	/**
	 * Field names should be quoted. We should handle that failure gracefully.
	 */
	@Test
	public void testCreateProjectNoQuotes() {
		Response r = asUser().body("{ name: \"some stupid project\" }").expect().response()
				.statusCode(HttpStatus.BAD_REQUEST.value()).when().post(PROJECTS);
		assertTrue(r.getBody().asString().contains("double quotes"));
	}

	@Test
	public void testCreateProject() {
		Map<String, String> project = new HashMap<>();
		project.put("name", "new project");

		Response r = asUser().and().body(project).expect().response().statusCode(HttpStatus.CREATED.value()).when()
				.post(PROJECTS);
		String location = r.getHeader(HttpHeaders.LOCATION);
		assertNotNull(location);
		assertTrue(location.startsWith("http://localhost:8080/projects/"));
		String responseBody = asUser().get(location).asString();
		String projectUsersLocation = from(responseBody).get("resource.links.find{it.rel=='project/users'}.href");
		// confirm that the current user was added to the project.
		asUser().expect().body("resource.resources.username", hasItem("fbristow")).when().get(projectUsersLocation);
	}

	@Test
	public void testGetProject() {
		asUser().expect().body("resource.name", equalTo("project22")).and()
				.body("resource.links.rel", hasItems("self", "project/users", "project/samples")).when().get(PROJECTS + "/5");
	}

	@Test
	public void testUpdateProjectName() {
		Map<String, String> project = new HashMap<>();
		String updatedName = "updated new project";
		String location = PROJECTS + "/5";
		project.put("name", updatedName);
		asUser().body(project).expect().statusCode(HttpStatus.OK.value()).when().patch(location);
		asUser().expect().body("resource.name", equalTo(updatedName)).when().get(location);
	}

	@Test
	public void testGetProjects() {
		// first page shouldn't have prev link, default view returns 20 projects
		asAdmin().expect().body("resource.links.rel", hasItems("self")).when().get(PROJECTS);
	}

	@Test
	public void testDeleteProject() {
		String projectUri = "http://localhost:8080/projects/5";
		asUser().expect().body("resource.links.rel", hasItems("collection")).and()
				.body("resource.links.href", hasItems("http://localhost:8080/projects")).when().delete(projectUri);
	}

	/**
	 * Make sure that if we issue a HEAD request on a resource, the request
	 * succeeds. We have two valid endpoints where a client can get data from
	 * (provided that they use the correct Accept headers). Make sure that both
	 * work.
	 */
	@Test
	public void verifyExistenceOfProjectWithHEAD() {
		String projectUri = "http://localhost:8080/projects/5";
		asUser().expect().statusCode(HttpStatus.OK.value()).when().head(projectUri);
		asUser().given().header("Accept", MediaType.JSON_UTF_8.toString()).expect().statusCode(HttpStatus.OK.value())
				.when().head(projectUri);
	}
}
