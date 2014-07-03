package ca.corefacility.bioinformatics.irida.web.controller.test.integration.sequencingrun;

import static ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils.asAdmin;
import static ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils.asSequencer;
import static ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils.asUser;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;

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

import ca.corefacility.bioinformatics.irida.config.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

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
public class SequencingRunIntegrationTest {

	// @Test
	public void testGetAllUsers() {
		asAdmin().expect().body("resource.links.rel", hasItems("self")).and()
				.body("resource.resources.username", hasItem("fbristow")).when().get("/users");
	}

	@Test
	public void testCreateRunAsAdminSucceed() {
		Map<String, String> run = createRun();
		asAdmin().given().body(run).expect().response().statusCode(HttpStatus.SC_CREATED).when()
				.post("/sequencingrun/miseqrun");
	}

	@Test
	public void testCreateRunAsUserFail() {
		Map<String, String> run = createRun();
		asUser().given().body(run).expect().response().statusCode(HttpStatus.SC_FORBIDDEN).when()
				.post("/sequencingrun/miseqrun");
	}

	@Test
	public void testCreateRunAsSequencerSucceed() {
		Map<String, String> run = createRun();
		asSequencer().given().body(run).expect().response().statusCode(HttpStatus.SC_CREATED).when()
				.post("/sequencingrun/miseqrun");
	}
	
	@Test
	public void testPostSequencingRunFail() {
		Map<String, String> run = createRun();
		asSequencer().given().body(run).expect().response().statusCode(HttpStatus.SC_BAD_REQUEST).when()
				.post("/sequencingrun");
	}

	private Map<String, String> createRun() {
		Map<String, String> run = new HashMap<>();
		run.put("workflow", "a test workflow");
		run.put("description", "a cool miseq run");
		return run;
	}
}
