package ca.corefacility.bioinformatics.irida.web.controller.test.integration.project;

import static ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils.asUser;
import static ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils.asAdmin;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import com.google.common.net.HttpHeaders;
import com.jayway.restassured.response.Response;

/**
 * Integration tests for project samples.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class ProjectSamplesIntegrationTest {

	@Test
	public void testCopySampleToProject() {
		final Map<String, String> sample = new HashMap<>();
		sample.put("sampleName", "sample_1");
		sample.put("sequencerSampleId", "sample_1");

		final String projectUri = "/projects/4";
		final String projectJson = asUser().get(projectUri).asString();
		final String samplesUri = from(projectJson).get("resource.links.find{it.rel == 'project/samples'}.href");

		final Response r = asUser().body(sample).expect().response().statusCode(HttpStatus.CREATED.value()).when()
				.put(samplesUri + "/5");
		final String location = r.getHeader(HttpHeaders.LOCATION);
		assertNotNull("Location should not be null.", location);
		assertEquals("The project/sample location uses the wrong sample ID.",
				"http://localhost:8080/projects/4/samples/5", location);
	}

	@Test
	public void testAddSampleToProject() {
		Map<String, String> sample = new HashMap<>();
		sample.put("sampleName", "sample_1");
		sample.put("sequencerSampleId", "sample_1");

		// load a project
		String projectUri = "/projects/1";
		// get the uri for creating samples associated with the project.
		String projectJson = asUser().get(projectUri).asString();
		String samplesUri = from(projectJson).get("resource.links.find{it.rel == 'project/samples'}.href");

		// post that uri
		Response r = asUser().body(sample).expect().response().statusCode(HttpStatus.CREATED.value()).when()
				.post(samplesUri);

		// check that the locations are set appropriately.
		String location = r.getHeader(HttpHeaders.LOCATION);

		assertNotNull(location);
		assertTrue(location.matches("^http://localhost:8080/projects/[0-9]+/samples/[0-9]+$"));
	}

	@Test
	public void testDeleteSampleFromProject() {
		String projectUri = "http://localhost:8080/projects/4";

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
		String projectUri = "http://localhost:8080/projects/5";
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
}
