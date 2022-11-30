package ca.corefacility.bioinformatics.irida.web.controller.test.integration.project;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.LocalHostUriTemplateHandler;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.annotation.RestIntegrationTest;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.Lists;
import com.google.common.net.HttpHeaders;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils.*;
import static io.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for project samples.
 */
@RestIntegrationTest
@TestExecutionListeners({
		DependencyInjectionTestExecutionListener.class,
		DbUnitTestExecutionListener.class,
		WithSecurityContextTestExecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/web/controller/test/integration/project/ProjectSamplesIntegrationTest.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class ProjectSamplesIT {
	private static final Logger logger = LoggerFactory.getLogger(ProjectSamplesIT.class);

	@Autowired
	SampleService sampleService;
	@Autowired
	ProjectService projectService;
	@Autowired
	LocalHostUriTemplateHandler uriTemplateHandler;

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testShareSampleToProjectWithoutOwnershipDefault() {
		Long projectId = 4L;
		Long sampleId = 1L;
		final List<Long> samples = Lists.newArrayList(sampleId);

		final String projectUri = "/api/projects/" + projectId;
		final String projectJson = asUser().get(projectUri).asString();
		final String samplesUri = from(projectJson).get("resource.links.find{it.rel == 'project/samples'}.href");

		assertTrue(samplesUri.endsWith("/api/projects/" + projectId + "/samples"),
				"The samples URI should end with /api/projects/4/samples");
		final Response r = asUser().contentType(ContentType.JSON)
				.body(samples)
				.header("Content-Type", "application/idcollection+json")
				.expect()
				.response()
				.statusCode(HttpStatus.CREATED.value())
				.when()
				.post(samplesUri);
		final String location = r.getHeader(HttpHeaders.LOCATION);
		assertNotNull(location, "Location should not be null.");
		assertEquals(uriTemplateHandler.getRootUri() + "/api/projects/4/samples/1", location,
				"The project/sample location uses the wrong sample ID.");

		Project project = projectService.read(projectId);

		ProjectSampleJoin sampleForProject = sampleService.getSampleForProject(project, sampleId);
		assertFalse(sampleForProject.isOwner(), "Project should not be owner of this sample");
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testShareSampleToProjectWithOwnership() {
		Long projectId = 4L;
		Long sampleId = 1L;
		final List<Long> samples = Lists.newArrayList(sampleId);

		final String projectUri = "/api/projects/" + projectId;
		final String projectJson = asUser().get(projectUri).asString();
		String samplesUri = from(projectJson).get("resource.links.find{it.rel == 'project/samples'}.href");

		assertTrue(samplesUri.endsWith("/api/projects/" + projectId + "/samples"),
				"The samples URI should end with /api/projects/4/samples");

		// adding ownership flag
		samplesUri = samplesUri + "?ownership=true";

		final Response r = asUser().contentType(ContentType.JSON)
				.body(samples)
				.header("Content-Type", "application/idcollection+json")
				.expect()
				.response()
				.statusCode(HttpStatus.CREATED.value())
				.when()
				.post(samplesUri);
		final String location = r.getHeader(HttpHeaders.LOCATION);
		assertNotNull(location, "Location should not be null.");
		assertEquals(uriTemplateHandler.getRootUri() + "/api/projects/4/samples/1", location,
				"The project/sample location uses the wrong sample ID.");

		Project project = projectService.read(projectId);

		ProjectSampleJoin sampleForProject = sampleService.getSampleForProject(project, sampleId);
		assertTrue(sampleForProject.isOwner(), "Project should be owner of this sample");
	}

	@Test
	public void testShareSampleToProjectWithSameId() {
		final List<String> samples = Lists.newArrayList("1");

		final String projectUri = "/api/projects/1";
		final String projectJson = asUser().get(projectUri).asString();
		final String samplesUri = from(projectJson).get("resource.links.find{it.rel == 'project/samples'}.href");

		// this used to return CONFLICT, but ended up in errors where samples
		// were partially added. Now it accepts the POST but doesn't change
		// anything since the sample is already there
		asUser().contentType(ContentType.JSON)
				.body(samples)
				.header("Content-Type", "application/idcollection+json")
				.expect()
				.response()
				.statusCode(HttpStatus.CREATED.value())
				.when()
				.post(samplesUri);
	}

	@Test
	public void testAddMultithreadedSamplesToProject() throws InterruptedException {
		final int numberOfThreads = 40;
		final int numberOfSamples = 40;
		final ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
		// load a project
		final String projectUri = "/api/projects/1";
		// get the uri for creating samples associated with the project.
		final String projectJson = asUser().get(projectUri).asString();
		final String samplesUri = from(projectJson).get("resource.links.find{it.rel == 'project/samples'}.href");

		final Callable<List<Integer>> task = () -> {
			final List<Integer> responses = new CopyOnWriteArrayList<>();
			final Random rand = new Random(Thread.currentThread().getId());

			for (int i = 0; i < numberOfSamples; i++) {
				final String sampleName = Thread.currentThread().getName() + "-" + rand.nextInt();
				final HttpClient client = HttpClientBuilder.create().build();
				final HttpPost request = new HttpPost(samplesUri);
				request.addHeader("Authorization", "Bearer " + ITestAuthUtils.getTokenForRole("user"));
				request.addHeader("Content-Type", "application/json");
				final StringEntity content = new StringEntity(
						"{\"sampleName\": \"" + sampleName + "\", \"sequencerSampleId\": \"" + sampleName + "\"}");
				request.setEntity(content);
				final HttpResponse response = client.execute(request);

				// post that uri
				responses.add(response.getStatusLine().getStatusCode());
			}
			return responses;
		};

		final List<Future<List<Integer>>> futures = new CopyOnWriteArrayList<>();
		for (int i = 0; i < numberOfThreads; i++) {
			futures.add(executorService.submit(task));
		}

		for (final Future<List<Integer>> f : futures) {
			try {
				final List<Integer> responses = f.get();
				assertTrue(responses.stream().allMatch(r -> r == HttpStatus.CREATED.value()),
						"All responses should be created.");
			} catch (InterruptedException | ExecutionException e) {
				logger.error("Failed to submit multiple samples simultaneously:", e);
				fail("Failed to submit multiple samples simultaneously.");
			}
		}

		executorService.shutdown();
		executorService.awaitTermination(10, TimeUnit.SECONDS);
	}

	@Test
	public void testAddSampleToProject() {
		Map<String, String> sample = new HashMap<>();
		sample.put("sampleName", "sample_1");
		sample.put("sequencerSampleId", "sample_1");

		// load a project
		String projectUri = uriTemplateHandler.getRootUri() + "/api/projects/1";
		// get the uri for creating samples associated with the project.
		String projectJson = asUser().get(projectUri).asString();
		String samplesUri = from(projectJson).get("resource.links.find{it.rel == 'project/samples'}.href");

		// post that uri
		Response r = asUser().body(sample)
				.expect()
				.response()
				.statusCode(HttpStatus.CREATED.value())
				.when()
				.post(samplesUri);

		// check that the locations are set appropriately.
		String location = r.getHeader(HttpHeaders.LOCATION);

		assertNotNull(location);
		assertTrue(location.matches("^" + uriTemplateHandler.getRootUri() + "/api/samples/[0-9]+$"));
	}

	@Test
	public void testDeleteSampleFromProject() {
		String projectUri = uriTemplateHandler.getRootUri() + "/api/projects/4";

		// load the project
		String projectJson = asUser().get(projectUri).asString();
		String projectSamplesUri = from(projectJson).get("resource.links.find{it.rel=='project/samples'}.href");
		String projectSamplesJson = asUser().get(projectSamplesUri).asString();
		String sampleLabel = from(projectSamplesJson).get("resource.resources[0].sampleName");
		// get the uri for a specific sample associated with a project
		String sampleUri = from(projectSamplesJson)
				.get("resource.resources[0].links.find{it.rel == 'project/sample'}.href");
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
		asUser().expect()
				.body("resource.resources.sampleName", not(hasItem(sampleLabel)))
				.when()
				.get(projectSamplesUri);
	}

	@Test
	public void testUpdateProjectSample() {
		String projectSampleUri = uriTemplateHandler.getRootUri() + "/api/samples/1";
		Map<String, String> updatedFields = new HashMap<>();
		String updatedName = "Totally-different-sample-name";
		updatedFields.put("sampleName", updatedName);

		asUser().and()
				.body(updatedFields)
				.expect()
				.body("resource.links.rel", hasItems("self", "sample/sequenceFiles"))
				.when()
				.patch(projectSampleUri);

		// now confirm that the sample name was updated
		asUser().expect().body("resource.sampleName", is(updatedName)).when().get(projectSampleUri);
	}

	@Test
	public void testUpdateProjectSampleCollectionDate() {
		String projectSampleUri = uriTemplateHandler.getRootUri() + "/api/samples/1";
		Map<String, String> updatedFields = new HashMap<>();
		String badDate = "x-y-z";
		updatedFields.put("collectionDate", badDate);

		// ensure updating date fails with bad date format
		asUser().and()
				.body(updatedFields)
				.expect()
				.response()
				.statusCode(HttpStatus.BAD_REQUEST.value())
				.when()
				.patch(projectSampleUri);

		String goodDate = "2021-10-12";
		updatedFields.put("collectionDate", goodDate);
		asUser().and()
				.body(updatedFields)
				.expect()
				.response()
				.statusCode(HttpStatus.OK.value())
				.when()
				.patch(projectSampleUri);

		// now confirm that the collectionDate was updated
		asUser().expect().body("resource.collectionDate", is(goodDate)).when().get(projectSampleUri);
	}

	@Test
	public void testReadSampleAsAdmin() {
		String projectUri = uriTemplateHandler.getRootUri() + "/api/projects/5";
		String projectSampleUri = projectUri + "/samples/1";

		asAdmin().expect()
				.body("resource.links.rel", hasItems("self", "sample/project", "sample/sequenceFiles"))
				.when()
				.get(projectSampleUri);
	}

	@Test
	public void testReadSamplesAsAdmin() {
		String projectUri = uriTemplateHandler.getRootUri() + "/api/projects/5";
		String projectSamplesUri = projectUri + "/samples";

		asAdmin().expect()
				.body("resource.resources.sampleName", hasItems("sample1", "sample2"))
				.when()
				.get(projectSamplesUri);
	}

	@Test
	public void testReadSampleCollectionDate() {
		String projectUri = uriTemplateHandler.getRootUri() + "/api/projects/5";
		String projectSampleUri = projectUri + "/samples/1";

		asAdmin().expect().body("resource.collectionDate", is("2019-01-24")).when().get(projectSampleUri);
	}

	@Test
	public void testReadSampleCollectionDate2() {
		String projectUri = uriTemplateHandler.getRootUri() + "/api/projects/5";
		String projectSampleUri = projectUri + "/samples/3";

		asAdmin().expect().body("resource.collectionDate", is("1999-12-05")).when().get(projectSampleUri);
	}

	@Test
	public void testReadSampleAsAdminWithDoubledUpSlashes() {
		String projectUri = uriTemplateHandler.getRootUri() + "/api//projects/5";
		String projectSampleUri = projectUri + "/samples/1";

		final Response r = asAdmin().expect()
				.body("resource.links.rel", hasItems("self", "sample/project", "sample/sequenceFiles"))
				.given()
				.urlEncodingEnabled(false)
				.when()
				.get(projectSampleUri);
		final String responseBody = r.getBody().asString();
		final String samplesUri = from(responseBody).get("resource.links.find{it.rel == 'sample/project'}.href");
		// now verify that we can actually get this (so doubled slash should not
		// have affected the link)
		asAdmin().expect().statusCode(HttpStatus.OK.value()).when().get(samplesUri);
	}

	@Test
	public void testReadSampleAsSequencer() {
		String projectUri = uriTemplateHandler.getRootUri() + "/api/projects/5";
		String projectSampleUri = projectUri + "/samples/1";

		asSequencer().expect()
				.body("resource.links.rel", hasItems("self", "sample/project", "sample/sequenceFiles"))
				.when()
				.get(projectSampleUri);
	}

	@Test
	public void testReadSamplesAsSequencer() {
		String projectUri = uriTemplateHandler.getRootUri() + "/api/projects/5";
		String projectSamplesUri = projectUri + "/samples";

		asSequencer().expect()
				.body("resource.resources.sampleName", hasItems("sample1", "sample2"))
				.when()
				.get(projectSamplesUri);
	}
}
