package ca.corefacility.bioinformatics.irida.web.controller.test.integration.project;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestSystemProperties;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import static ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils.*;
import static org.hamcrest.Matchers.hasItems;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiJdbcDataSourceConfig.class,
		IridaApiPropertyPlaceholderConfig.class, IridaApiServicesConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@ActiveProfiles("it")
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
		asAdmin().get(ITestSystemProperties.BASE_URL + ANALYSIS_PROJECT_BASE).then().statusCode(HttpStatus.OK.value())
				.body("resource.resources.identifier", hasItems("1", "2", "3"));
	}

	@Test
	public void testGetProjectAnalysisAsUser() {
		asUser().get(ITestSystemProperties.BASE_URL + ANALYSIS_PROJECT_BASE).then()
				.body("resource.resources.identifier", hasItems("1", "2", "3"));
	}

	@Test
	public void testGetProjectAnalysisAsOtherUser() {
		asOtherUser().get(ITestSystemProperties.BASE_URL + ANALYSIS_PROJECT_BASE).then()
				.statusCode(HttpStatus.FORBIDDEN.value());
	}

	@Test
	public void testGetProjectAnalysisByTypeAsAdmin() {
		asAdmin().get(ITestSystemProperties.BASE_URL + ANALYSIS_SISTR_BASE).then().statusCode(HttpStatus.OK.value())
				.body("resource.resources.identifier", hasItems("2", "3"));
	}

	@Test
	public void testGetProjectAnalysisByTypeUser() {
		asUser().get(ITestSystemProperties.BASE_URL + ANALYSIS_SISTR_BASE).then().statusCode(HttpStatus.OK.value())
				.body("resource.resources.identifier", hasItems("2", "3"));
	}

	@Test
	public void testGetProjectAnalysisByTypeAsOtherUser() {
		asOtherUser().get(ITestSystemProperties.BASE_URL + ANALYSIS_SISTR_BASE).then()
				.statusCode(HttpStatus.FORBIDDEN.value());
	}
}
