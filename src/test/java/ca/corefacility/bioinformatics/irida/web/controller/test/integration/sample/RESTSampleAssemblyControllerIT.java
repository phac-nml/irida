package ca.corefacility.bioinformatics.irida.web.controller.test.integration.sample;

import ca.corefacility.bioinformatics.irida.config.data.IridaDbUnitConfig;
import ca.corefacility.bioinformatics.irida.web.controller.api.samples.RESTSampleAssemblyController;
import ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestSystemProperties;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import io.restassured.response.Response;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import static ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils.asUser;
import static io.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Import(IridaDbUnitConfig.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/web/controller/test/integration/sample/RESTSampleAssemblyControllerIT.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
/**
 * IT test for the {@link RESTSampleAssemblyController}
 */ public class RESTSampleAssemblyControllerIT {

	@Test
	public void testListAssemblies() {
		String sampleUri = ITestSystemProperties.BASE_URL + "/api/projects/5/samples/1";
		Response response = asUser().expect()
				.statusCode(HttpStatus.OK.value())
				.when()
				.get(sampleUri);

		String sampleBody = response.getBody()
				.asString();

		String assembliesHref = from(sampleBody).getString(
				"resource.links.find{it.rel == '" + RESTSampleAssemblyController.REL_SAMPLE_ASSEMBLIES + "'}.href");

		Response assemblyListResponse = asUser().expect()
				.statusCode(HttpStatus.OK.value())
				.and()
				.body("resource.resources[0].file", equalTo("/tmp/analysis-files/contigs.fasta"))
				.when()
				.get(assembliesHref);

		String listBody = assemblyListResponse.getBody()
				.asString();

		String singleAssemblyHref = from(listBody).getString("resource.resources[0].links.find{it.rel == 'self'}.href");

		asUser().expect()
				.statusCode(HttpStatus.OK.value())
				.and()
				.body("resource.file", equalTo("/tmp/analysis-files/contigs.fasta"))
				.when()
				.get(singleAssemblyHref);

	}
}
