package ca.corefacility.bioinformatics.irida.web.controller.test.integration.project;

import ca.corefacility.bioinformatics.irida.config.IridaIntegrationTestUriConfig;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import static ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils.*;
import static org.hamcrest.Matchers.hasItems;

@Tag("IntegrationTest") @Tag("Rest")
@ActiveProfiles("it")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(IridaIntegrationTestUriConfig.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/web/controller/test/integration/project/RESTProjectAnalysisControllerIT.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
/**
 * Test for functions of {@link RESTProjectAnalysisControllerIT}
 */
public class RESTProjectAnalysisControllerIT {

	public static final String ANALYSIS_PROJECT_BASE = "/api/projects/1/analyses";
	public static String ANALYSIS_SISTR_BASE = "/api/projects/1/analyses/sistr";

	@Test
	public void testGetProjectAnalysisAsAdmin() {
		asAdmin().get(ANALYSIS_PROJECT_BASE).then().statusCode(HttpStatus.OK.value())
				.body("resource.resources.identifier", hasItems("1", "2", "3"));
	}

	@Test
	public void testGetProjectAnalysisAsUser() {
		asUser().get(ANALYSIS_PROJECT_BASE).then()
				.body("resource.resources.identifier", hasItems("1", "2", "3"));
	}

	@Test
	public void testGetProjectAnalysisAsOtherUser() {
		asOtherUser().get(ANALYSIS_PROJECT_BASE).then()
				.statusCode(HttpStatus.FORBIDDEN.value());
	}

	@Test
	public void testGetProjectAnalysisByTypeAsAdmin() {
		asAdmin().get(ANALYSIS_SISTR_BASE).then().statusCode(HttpStatus.OK.value())
				.body("resource.resources.identifier", hasItems("2", "3"));
	}

	@Test
	public void testGetProjectAnalysisByTypeUser() {
		asUser().get(ANALYSIS_SISTR_BASE).then().statusCode(HttpStatus.OK.value())
				.body("resource.resources.identifier", hasItems("2", "3"));
	}

	@Test
	public void testGetProjectAnalysisByTypeAsOtherUser() {
		asOtherUser().get(ANALYSIS_SISTR_BASE).then()
				.statusCode(HttpStatus.FORBIDDEN.value());
	}
}
