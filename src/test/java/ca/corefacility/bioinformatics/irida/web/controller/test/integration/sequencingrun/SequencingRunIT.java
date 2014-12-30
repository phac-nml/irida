package ca.corefacility.bioinformatics.irida.web.controller.test.integration.sequencingrun;

import static ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils.asAdmin;
import static ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils.asSequencer;
import static ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils.asUser;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.model.enums.SequencingRunUploadStatus;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.ImmutableMap;

/**
 * Integration tests for users.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiJdbcDataSourceConfig.class,
		IridaApiPropertyPlaceholderConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/web/controller/test/integration/sequencingrun/SequencingRunIntegrationTest.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class SequencingRunIT {

	// TODO: When more run types are available test that they are represented in
	// listing and reading

	@Test
	public void testListRuns() {
		asAdmin().expect().and().body("resource.resources.description", hasItems("run 1", "run 2", "run 3")).and()
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
				.post("/api/sequencingrun/miseqrun");
	}

	@Test
	public void testCreateRunAsUserFail() {
		Map<String, String> run = createRun();
		asUser().given().body(run).expect().response().statusCode(HttpStatus.SC_FORBIDDEN).when()
				.post("/api/sequencingrun/miseqrun");
	}

	@Test
	public void testCreateRunAsSequencerSucceed() {
		Map<String, String> run = createRun();
		asSequencer().given().body(run).expect().response().statusCode(HttpStatus.SC_CREATED).when()
				.post("/api/sequencingrun/miseqrun");
	}

	@Test
	public void testPostSequencingRunFail() {
		Map<String, String> run = createRun();
		asSequencer().given().body(run).expect().response().statusCode(HttpStatus.SC_BAD_REQUEST).when()
				.post("/api/sequencingrun");
	}

	@Test
	public void testUpdateSequencingRunStatus() {
		Map<String, String> run = createRun();
		// create the run and get the location
		String location = asSequencer().given().body(run).expect().response().statusCode(HttpStatus.SC_CREATED).when()
				.post("/api/sequencingrun/miseqrun").then().extract().header("Location");

		asAdmin().expect().body("resource.uploadStatus", is(SequencingRunUploadStatus.UPLOADING.toString())).when().get(location);

		Map<String, String> updateProperties = ImmutableMap.of("uploadStatus",
				SequencingRunUploadStatus.COMPLETE.toString());

		asSequencer().given().body(updateProperties).expect().response().statusCode(HttpStatus.SC_OK).when()
				.patch(location);

		asAdmin().expect().body("resource.uploadStatus", is(SequencingRunUploadStatus.COMPLETE.toString())).when().get(location);

	}

	private Map<String, String> createRun() {
		Map<String, String> run = new HashMap<>();
		run.put("workflow", "a test workflow");
		run.put("description", "a cool miseq run");
		return run;
	}
}
