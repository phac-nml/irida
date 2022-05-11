package ca.corefacility.bioinformatics.irida.web.controller.test.integration.sample;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.annotation.RestIntegrationTest;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.ImmutableMap;

import static ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils.asUser;
import static io.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.*;

@RestIntegrationTest
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/web/controller/test/integration/sample/RESTSampleMetadataControllerIT.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class RESTSampleMetadataControllerIT {

	@Test
	public void testGetMetadataForProject() {
		Long projectId = 5L;

		final String projectUri = "/api/projects/" + projectId;
		final String projectJson = asUser().expect().statusCode(HttpStatus.OK.value()).when().get(projectUri)
				.asString();

		final String samplesUri = from(projectJson).get("resource.links.find{it.rel == 'project/samples'}.href");

		String samplesJson = asUser().get(samplesUri).asString();

		final String metadataUri = from(samplesJson)
				.get("resource.links.find{it.rel == 'project/samples/metadata'}.href");

		asUser().expect().statusCode(HttpStatus.OK.value()).and().body("resource.resources.metadata", hasSize(2)).and()
				.body("resource.resources.metadata[0]", hasKey("field1")).and()
				.body("resource.resources.metadata[0]", hasKey("field2")).when().get(metadataUri).asString();

	}

	@Test
	public void testGetMetadataForSample() {
		Long sampleId = 1L;

		final String sampleUri = "/api/samples/" + sampleId;
		final String sampleJson = asUser().expect().statusCode(HttpStatus.OK.value()).when().get(sampleUri).asString();

		final String metadataUri = from(sampleJson).get("resource.links.find{it.rel == 'sample/metadata'}.href");

		asUser().expect().statusCode(HttpStatus.OK.value()).and().body("resource.metadata", hasKey("field1")).and()
				.body("resource.metadata", hasKey("field2")).when().get(metadataUri).asString();

	}

	@Test
	public void testPostMetadata() {
		Long sampleId = 1L;

		final String sampleUri = "/api/samples/" + sampleId;
		final String sampleJson = asUser().expect().statusCode(HttpStatus.OK.value()).when().get(sampleUri).asString();

		String newKeyName = "somethingnew";

		final String metadataUri = from(sampleJson).get("resource.links.find{it.rel == 'sample/metadata'}.href");

		Map<String, Map<String, String>> metadata = ImmutableMap.of(newKeyName,
				ImmutableMap.of("type", "text", "value", "newval"));

		asUser().body(metadata).expect().body("resource.metadata", hasKey(newKeyName)).and()
				.body("resource.metadata", not(hasKey("field1"))).and().body("resource.metadata", not(hasKey("field2")))
				.when().post(metadataUri);
	}

	@Test
	public void testOverwritePost() {
		Long sampleId = 1L;

		final String sampleUri = "/api/samples/" + sampleId;
		final String sampleJson = asUser().expect().statusCode(HttpStatus.OK.value()).when().get(sampleUri).asString();

		final String metadataUri = from(sampleJson).get("resource.links.find{it.rel == 'sample/metadata'}.href");

		Map<String, Map<String, String>> metadata = ImmutableMap.of("field1",
				ImmutableMap.of("type", "text", "value", "newval"));

		asUser().body(metadata).expect().body("resource.metadata", hasKey("field1")).and()
				.body("resource.metadata", not(hasKey("field2"))).and()
				.body("resource.metadata.field1.value", equalTo("newval")).when().post(metadataUri);
	}

	@Test
	public void testPutMetadata() {
		Long sampleId = 1L;

		final String sampleUri = "/api/samples/" + sampleId;
		final String sampleJson = asUser().expect().statusCode(HttpStatus.OK.value()).when().get(sampleUri).asString();

		String newKeyName = "somethingnew";

		final String metadataUri = from(sampleJson).get("resource.links.find{it.rel == 'sample/metadata'}.href");

		Map<String, Map<String, String>> metadata = ImmutableMap.of(newKeyName,
				ImmutableMap.of("type", "text", "value", "newval"));

		asUser().body(metadata).expect().body("resource.metadata", hasKey(newKeyName)).and()
				.body("resource.metadata", hasKey("field1")).and().body("resource.metadata", hasKey("field1")).when()
				.put(metadataUri);
	}

	@Test
	public void testOverwritePut() {
		Long sampleId = 1L;

		final String sampleUri = "/api/samples/" + sampleId;
		final String sampleJson = asUser().expect().statusCode(HttpStatus.OK.value()).when().get(sampleUri).asString();

		final String metadataUri = from(sampleJson).get("resource.links.find{it.rel == 'sample/metadata'}.href");

		Map<String, Map<String, String>> metadata = ImmutableMap.of("field1",
				ImmutableMap.of("type", "text", "value", "newval"));

		asUser().body(metadata).expect().body("resource.metadata", hasKey("field1")).and()
				.body("resource.metadata", hasKey("field2")).and()
				.body("resource.metadata.field1.value", equalTo("newval")).when().put(metadataUri);
	}
}
