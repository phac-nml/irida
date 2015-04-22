package ca.corefacility.bioinformatics.irida.web.controller.test.integration.project;

import static ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils.asAdmin;
import static ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils.asUser;
import static ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils.asSequencer;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
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
import com.google.common.collect.Lists;
import com.google.common.net.HttpHeaders;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;

/**
 * Integration tests for project samples.
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiJdbcDataSourceConfig.class,
		IridaApiPropertyPlaceholderConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/web/controller/test/integration/project/ProjectSamplesIntegrationTest.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class ProjectSamplesIT {

	@Test
	public void testCopySampleToProject() {
		final List<String> samples = Lists.newArrayList("1");

		final String projectUri = "/api/projects/4";
		final String projectJson = asUser().get(projectUri).asString();
		final String samplesUri = from(projectJson).get("resource.links.find{it.rel == 'project/samples'}.href");

		assertTrue("The samples URI should end with /api/projects/4/samples",
				samplesUri.endsWith("/api/projects/4/samples"));
		final Response r = asUser().contentType(ContentType.JSON).body(samples)
				.header("Content-Type", "application/idcollection+json").expect().response()
				.statusCode(HttpStatus.CREATED.value()).when().post(samplesUri);
		final String location = r.getHeader(HttpHeaders.LOCATION);
		assertNotNull("Location should not be null.", location);
		assertEquals("The project/sample location uses the wrong sample ID.", ITestSystemProperties.BASE_URL
				+ "/api/projects/4/samples/1", location);
	}

	@Test
	public void testCopySampleToProjectWithSameId() {
		final List<String> samples = Lists.newArrayList("3");

		final String projectUri = "/api/projects/4";
		final String projectJson = asUser().get(projectUri).asString();
		final String samplesUri = from(projectJson).get("resource.links.find{it.rel == 'project/samples'}.href");

		asUser().contentType(ContentType.JSON).body(samples).header("Content-Type", "application/idcollection+json")
				.expect().response().statusCode(HttpStatus.CONFLICT.value()).when().post(samplesUri);
	}

	@Test
	public void testAddSampleToProject() {
		Map<String, String> sample = new HashMap<>();
		sample.put("sampleName", "sample_1");
		sample.put("sequencerSampleId", "sample_1");

		// load a project
		String projectUri = "/api/projects/1";
		// get the uri for creating samples associated with the project.
		String projectJson = asUser().get(projectUri).asString();
		String samplesUri = from(projectJson).get("resource.links.find{it.rel == 'project/samples'}.href");

		// post that uri
		Response r = asUser().body(sample).expect().response().statusCode(HttpStatus.CREATED.value()).when()
				.post(samplesUri);

		// check that the locations are set appropriately.
		String location = r.getHeader(HttpHeaders.LOCATION);

		assertNotNull(location);
		assertTrue(location.matches("^" + ITestSystemProperties.BASE_URL + "/api/projects/[0-9]+/samples/[0-9]+$"));
	}

	@Test
	public void testDeleteSampleFromProject() {
		String projectUri = ITestSystemProperties.BASE_URL + "/api/projects/4";

		// load the project
		String projectJson = asUser().get(projectUri).asString();
		String projectSamplesUri = from(projectJson).get("resource.links.find{it.rel=='project/samples'}.href");
		String projectSamplesJson = asUser().get(projectSamplesUri).asString();
		String sampleLabel = from(projectSamplesJson).get("resource.resources[0].sampleName");
		// get the uri for a specific sample associated with a project
		String sampleUri = from(projectSamplesJson).get("resource.resources[0].links.find{it.rel == 'self'}.href");
		// issue a delete against the service
		Response r = asAdmin().expect().statusCode(HttpStatus.OK.value()).when().delete(sampleUri);

		// check that the response body contains links to the project and
		// samples collection
		String responseBody = r.getBody().asString();
		String projectUriRel = from(responseBody).get("resource.links.find{it.rel == 'project'}.href");
		assertNotNull(projectUriRel);
		assertEquals(projectUri, projectUriRel);

		String samplesUri = from(responseBody).get("resource.links.find{it.rel == 'project/samples'}.href");
		assertNotNull(samplesUri);
		assertEquals(projectUri + "/samples", samplesUri);

		// now confirm that the sample is not there anymore
		asUser().expect().body("resource.resources.sampleName", not(hasItem(sampleLabel))).when()
				.get(projectSamplesUri);
	}

	@Test
	public void testUpdateProjectSample() {
		String projectUri = ITestSystemProperties.BASE_URL + "/api/projects/5";
		String projectSampleUri = projectUri + "/samples/1";
		Map<String, String> updatedFields = new HashMap<>();
		String updatedName = "Totally-different-sample-name";
		updatedFields.put("sampleName", updatedName);

		asUser().and().body(updatedFields).expect()
				.body("resource.links.rel", hasItems("self", "project", "sample/sequenceFiles")).when()
				.patch(projectSampleUri);

		// now confirm that the sample name was updated
		asUser().expect().body("resource.sampleName", is(updatedName)).when().get(projectSampleUri);
	}

	@Test
	public void testReadSampleAsAdmin() {
		String projectUri = ITestSystemProperties.BASE_URL + "/api/projects/5";
		String projectSampleUri = projectUri + "/samples/1";

		asAdmin().expect().body("resource.links.rel", hasItems("self", "sample/project", "sample/sequenceFiles"))
				.when().get(projectSampleUri);
	}

	@Test
	public void testReadSampleAsSequencer() {
		String projectUri = ITestSystemProperties.BASE_URL + "/api/projects/5";
		String projectSampleUri = projectUri + "/samples/1";

		asSequencer().expect().body("resource.links.rel", hasItems("self", "sample/project", "sample/sequenceFiles"))
				.when().get(projectSampleUri);
	}
}
