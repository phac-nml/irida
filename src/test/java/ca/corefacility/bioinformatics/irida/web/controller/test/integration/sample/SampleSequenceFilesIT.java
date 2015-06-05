package ca.corefacility.bioinformatics.irida.web.controller.test.integration.sample;

import static ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils.asAdmin;
import static ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils.asUser;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestSystemProperties;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.net.HttpHeaders;
import com.jayway.restassured.response.Response;

/**
 * Integration tests for working with sequence files and samples.
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiJdbcDataSourceConfig.class,
		IridaApiPropertyPlaceholderConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/web/controller/test/integration/sample/SampleSequenceFilesIntegrationTest.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class SampleSequenceFilesIT {

	private static final byte[] FASTQ_FILE_CONTENTS = "@testread\nACGTACGT\n+\n????????".getBytes();

	@Test
	public void testAddSequenceFileToSample() throws IOException {
		String sampleUri = ITestSystemProperties.BASE_URL + "/api/projects/5/samples/1";
		Response response = asUser().expect().statusCode(HttpStatus.OK.value()).when().get(sampleUri);
		String sampleBody = response.getBody().asString();
		String sequenceFileUri = from(sampleBody).getString(
				"resource.links.find{it.rel == 'sample/sequenceFiles'}.href");
		// prepare a file for sending to the server
		Path sequenceFile = Files.createTempFile(null, null);
		Files.write(sequenceFile, FASTQ_FILE_CONTENTS);

		Map<String,String> fileParams = new HashMap<>();
		fileParams.put("description", "some file");
		
		Response r = asAdmin().given().contentType(MediaType.MULTIPART_FORM_DATA_VALUE).multiPart("file", sequenceFile.toFile())
				.multiPart("parameters",fileParams, MediaType.APPLICATION_JSON_VALUE)
				.expect().statusCode(HttpStatus.CREATED.value()).when().post(sequenceFileUri);

		// check that the location and link headers were created:
		String location = r.getHeader(HttpHeaders.LOCATION);
		assertNotNull("location must exist",location);
		assertTrue("location must be correct",location.matches(sequenceFileUri + "/[0-9]+"));
		// confirm that the sequence file was added to the sample sequence files list
		asUser().expect().body("resource.resources.fileName",hasItem(sequenceFile.getFileName().toString()))
			.and().body("resource.resources.links[0].rel",hasItems("self"))
			.when().get(sequenceFileUri);
		String responseBody = asUser().get(location).asString();
		assertTrue("Result of POST must equal result of GET",r.asString().equals(responseBody));
		// clean up
		Files.delete(sequenceFile);
	}
	
	@Test
	public void testAddSequenceFilePairToSample() throws IOException {
		String sampleUri = ITestSystemProperties.BASE_URL + "/api/projects/5/samples/1";
		Response response = asUser().expect().statusCode(HttpStatus.OK.value()).when().get(sampleUri);
		String sampleBody = response.getBody().asString();
		String sequenceFileUri = from(sampleBody).getString(
				"resource.links.find{it.rel == 'sample/sequenceFiles'}.href");

		String sequenceFilePairUri = ITestSystemProperties.BASE_URL + "/api/projects/5/samples/1/sequenceFilePairs";

		Path sequenceFile = Files.createTempFile(null, null);
		Files.write(sequenceFile, FASTQ_FILE_CONTENTS);

		Map<String, String> fileParams = new HashMap<>();
		fileParams.put("description", "some file");

		Response r = asAdmin().given().contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
				.multiPart("file1", sequenceFile.toFile())
				.multiPart("parameters1", fileParams, MediaType.APPLICATION_JSON_VALUE)
				.multiPart("file2", sequenceFile.toFile())
				.multiPart("parameters2", fileParams, MediaType.APPLICATION_JSON_VALUE).expect()
				.statusCode(HttpStatus.CREATED.value()).when().post(sequenceFilePairUri);
		
		String location1 = r.body().jsonPath().get("resource.resources[0].links.find{it.rel == 'self'}.href");
		String location2 = r.body().jsonPath().get("resource.resources[1].links.find{it.rel == 'self'}.href");

		assertTrue("Response body must contain 1st sequence reference",location1.matches(sequenceFileUri + "/[0-9]+"));
		assertTrue("Response body must contain 2nd sequence reference",location2.matches(sequenceFileUri + "/[0-9]+"));

		assertNotEquals(location1, location2);

		// confirm the resource exist
		asUser().expect().body("resource.links.rel", hasItem("self")).when().get(location1);
		asUser().expect().body("resource.links.rel", hasItem("self")).when().get(location2);

		// clean up
		Files.delete(sequenceFile);
	}
	
	@Test
	public void testAddSequenceFileToSampleWithOptionalProperties() throws IOException {
		String sampleUri = ITestSystemProperties.BASE_URL + "/api/projects/5/samples/1";
		Response response = asUser().expect().statusCode(HttpStatus.OK.value()).when().get(sampleUri);
		String sampleBody = response.getBody().asString();
		String sequenceFileUri = from(sampleBody).getString(
				"resource.links.find{it.rel == 'sample/sequenceFiles'}.href");
		// prepare a file for sending to the server
		Path sequenceFile = Files.createTempFile(null, null);
		Files.write(sequenceFile, FASTQ_FILE_CONTENTS);

		String optionalValue = "some interesting information about this file";
		Map<String, String> fileParams = new HashMap<>();
		fileParams.put("description", "some file");
		fileParams.put("optionalProperty", optionalValue);

		Response r = asAdmin().given().contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
				.multiPart("file", sequenceFile.toFile())
				.multiPart("parameters", fileParams, MediaType.APPLICATION_JSON_VALUE).expect()
				.statusCode(HttpStatus.CREATED.value()).when().post(sequenceFileUri);

		// check that the location and link headers were created:
		String location = r.getHeader(HttpHeaders.LOCATION);

		assertNotNull(location);
		assertTrue(location.matches(sequenceFileUri + "/[0-9]+"));

		// confirm that the sequence file contains the given optional property
		asAdmin().expect().body("resource.optionalProperty", equalTo(optionalValue)).when().get(location);

		// clean up
		Files.delete(sequenceFile);
	}

	@Test
	public void testRemoveSequenceFileFromSample() throws IOException {
		// for now, add a sequence file to the sample so that we can remove it
		String sampleUri = ITestSystemProperties.BASE_URL + "/api/projects/5/samples/1";
		Response response = asUser().expect().statusCode(HttpStatus.OK.value()).when().get(sampleUri);
		String sampleBody = response.getBody().asString();
		String sequenceFileUri = from(sampleBody).getString(
				"resource.links.find{it.rel == 'sample/sequenceFiles'}.href");
		// prepare a file for sending to the server
		Path sequenceFile = Files.createTempFile(null, null);
		Files.write(sequenceFile, FASTQ_FILE_CONTENTS);

		Response r = asAdmin().given().contentType(MediaType.MULTIPART_FORM_DATA_VALUE).multiPart("file", sequenceFile.toFile())
				.expect().statusCode(HttpStatus.CREATED.value()).when().post(sequenceFileUri);

		String location = r.getHeader(HttpHeaders.LOCATION);

		r = asAdmin().expect().statusCode(HttpStatus.OK.value()).when().delete(location);
		String responseBody = r.getBody().asString();
		String sampleLocation = from(responseBody).getString("resource.links.find{it.rel == 'sample'}.href");

		assertNotNull(sampleLocation);
		assertEquals(sampleUri, sampleLocation);
	}

	@Test
	public void testSequenceFilePermissionInInvalidSample() {
		String sampleUri = ITestSystemProperties.BASE_URL + "/api/projects/100/samples/1/sequenceFiles";
		asAdmin().expect().statusCode(HttpStatus.NOT_FOUND.value()).when().get(sampleUri);
	}
	
}
