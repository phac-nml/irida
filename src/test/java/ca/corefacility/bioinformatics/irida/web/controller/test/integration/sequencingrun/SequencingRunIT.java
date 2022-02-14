package ca.corefacility.bioinformatics.irida.web.controller.test.integration.sequencingrun;

import static ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils.asAdmin;
import static ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils.asSequencer;
import static ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils.asUser;
import static ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils.asOtherUser;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.annotation.RestIntegrationTest;
import ca.corefacility.bioinformatics.irida.model.enums.SequencingRunUploadStatus;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.ImmutableMap;

/**
 * Integration tests for users.
 * 
 */
@RestIntegrationTest
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/web/controller/test/integration/sequencingrun/SequencingRunIntegrationTest.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class SequencingRunIT {

	@Test
	public void testListRuns() {
		asAdmin().expect().and().body("resource.resources.description", hasItems("run 1", "run 2", "run 3")).and()
				.body("resource.resources.workflow", hasItems("Test workflow 1", "Test workflow 2", "Test workflow 3"))
				.and().when().get("/api/sequencingrun");
	}

	@Test
	public void testListRunsAsSequencer() {
		asSequencer().expect().and().body("resource.resources.description", hasItems("run 1", "run 2", "run 3")).and()
				.body("resource.resources.workflow", hasItems("Test workflow 1", "Test workflow 2", "Test workflow 3"))
				.and().when().get("/api/sequencingrun");
	}

	@Test
	public void testGetRun() {
		asAdmin().expect().body("resource.links.rel", hasItems("self")).and().body("resource.description", is("run 2"))
				.and().body("resource.workflow", is("Test workflow 2")).when().get("/api/sequencingrun/2");
	}

	@Test
	public void testCreateRunAsAdminSucceed() {
		Map<String, String> run = createRun();
		asAdmin().given().body(run).expect().response().statusCode(HttpStatus.SC_CREATED).when()
				.post("/api/sequencingrun");
	}

	@Test
	public void testCreateRunAsUserSuccess() {
		Map<String, String> run = createRun();
		asUser().given().body(run).expect().response().statusCode(HttpStatus.SC_CREATED).when()
				.post("/api/sequencingrun");
	}

	@Test
	public void testCreateRunAsSequencerSucceed() {
		Map<String, String> run = createRun();
		asSequencer().given().body(run).expect().response().statusCode(HttpStatus.SC_CREATED).when()
				.post("/api/sequencingrun");
	}

	@Test
	public void testCreateLegacyRunAsAdminSucceed() {
		Map<String, String> run = createRun();
		run.remove("sequencerType");
		asSequencer().given().body(run).expect().response().statusCode(HttpStatus.SC_CREATED).when()
				.post("/api/sequencingrun/miseqrun");
	}

	@Test
	public void testPostSequencingRunBadTypeFail() {
		Map<String, String> run = createRun();
		run.replace("sequencerType", "something bad");
		asSequencer().given().body(run).expect().response().statusCode(HttpStatus.SC_BAD_REQUEST).when()
				.post("/api/sequencingrun");
	}

	@Test
	public void testUpdateSequencingRunStatus() {
		Map<String, String> run = createRun();
		// create the run and get the location
		String location = asSequencer().given().body(run).expect().response().statusCode(HttpStatus.SC_CREATED).when()
				.post("/api/sequencingrun/miseqrun").then().extract().header("Location");

		// ensure the status is UPLOADING
		asAdmin().expect().body("resource.uploadStatus", is(SequencingRunUploadStatus.UPLOADING.toString())).when()
				.get(location);

		Map<String, String> updateProperties = ImmutableMap.of("uploadStatus",
				SequencingRunUploadStatus.COMPLETE.toString());

		// pust an update to COMPLETE
		asSequencer().given().body(updateProperties).expect().response().statusCode(HttpStatus.SC_OK).when()
				.patch(location);

		// ensure the status is COMPLETE
		asAdmin().expect().body("resource.uploadStatus", is(SequencingRunUploadStatus.COMPLETE.toString())).when()
				.get(location);

	}

	@Test
	public void testUpdateSequencingRunAsUser() {
		Map<String, String> run = createRun();
		// create the run and get the location
		String location = asUser().given().body(run).expect().response().statusCode(HttpStatus.SC_CREATED).when()
				.post("/api/sequencingrun/miseqrun").then().extract().header("Location");

		// ensure the status is UPLOADING
		asUser().expect().body("resource.uploadStatus", is(SequencingRunUploadStatus.UPLOADING.toString())).when()
				.get(location);

		Map<String, String> updateProperties = ImmutableMap.of("uploadStatus",
				SequencingRunUploadStatus.COMPLETE.toString());

		// patch an update to COMPLETE
		asUser().given().body(updateProperties).expect().response().statusCode(HttpStatus.SC_OK).when().patch(location);

		// ensure the status is COMPLETE
		asUser().expect().body("resource.uploadStatus", is(SequencingRunUploadStatus.COMPLETE.toString())).when()
				.get(location);

		// ensure other users can't write
		asOtherUser().given().body(updateProperties).expect().response().statusCode(HttpStatus.SC_FORBIDDEN).when()
				.patch(location);
	}

	private Map<String, String> createRun() {
		Map<String, String> run = new HashMap<>();
		run.put("workflow", "a test workflow");
		run.put("description", "a cool miseq run");
		run.put("layoutType", "SINGLE_END");
		run.put("sequencerType", "miseq");
		return run;
	}
}
