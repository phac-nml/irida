package ca.corefacility.bioinformatics.irida.web.controller.test.integration.sample;

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

import static ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils.asUser;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.hasKey;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiJdbcDataSourceConfig.class,
		IridaApiPropertyPlaceholderConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/web/controller/test/integration/sample/RESTSampleMetadataControllerIT.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class RESTSampleMetadataControllerIT {

	@Test
	public void testGetMetadataForProject() {
		Long projectId = 5L;

		final String projectUri = "/api/projects/" + projectId;
		final String projectJson = asUser().expect()
				.statusCode(HttpStatus.OK.value())
				.get(projectUri)
				.asString();

		final String samplesUri = from(projectJson).get("resource.links.find{it.rel == 'project/samples'}.href");

		String samplesJson = asUser().get(samplesUri)
				.asString();

		final String metadataUri = from(samplesJson).get("resource.links.find{it.rel == 'samples/metadata'}.href");

		asUser().expect()
				.statusCode(HttpStatus.OK.value())
				.and()
				.body("resource.resources.metadata", hasKey("field1"))
				.and()
				.body("resource.resources.metadata", hasKey("field2"))
				.when()
				.get(metadataUri)
				.asString();

	}
}
