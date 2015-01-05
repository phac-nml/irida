package ca.corefacility.bioinformatics.irida.web.controller.test.integration.sample;

import static ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils.asUser;
import static ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils.asAdmin;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
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
import ca.corefacility.bioinformatics.irida.web.controller.api.samples.RESTSampleSequenceFilesController;
import ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestSystemProperties;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.Lists;
import com.google.common.net.HttpHeaders;
import com.jayway.restassured.response.Response;

/**
 * Integration tests for working with sequence files and samples.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
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

		assertNotNull(location);
		assertTrue(location.matches(sequenceFileUri + "/[0-9]+"));
		
		// confirm that the sequence file was added to the sample sequence files list
		asUser().expect().body("resource.resources.fileName",hasItem(sequenceFile.getFileName().toString()))
			.and().body("resource.resources.links[0].rel",hasItems("self"))
			.when().get(sequenceFileUri);

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
	public void testAddExistingSequenceFileToSample() throws IOException {
		// for now, add a sequence file to another sample
		String sampleUri = ITestSystemProperties.BASE_URL + "/api/projects/5/samples/1";
		Response response = asUser().expect().statusCode(HttpStatus.OK.value()).when().get(sampleUri);
		String projectBody = response.getBody().asString();
		String sequenceFileUri = from(projectBody).getString(
				"resource.links.find{it.rel == 'sample/sequenceFiles'}.href");
		// prepare a file for sending to the server
		Path sequenceFile = Files.createTempFile(null, null);
		Files.write(sequenceFile, FASTQ_FILE_CONTENTS);

		Response r = asAdmin().given().contentType(MediaType.MULTIPART_FORM_DATA_VALUE).multiPart("file", sequenceFile.toFile())
				.expect().statusCode(HttpStatus.CREATED.value()).when().post(sequenceFileUri);

		// figure out what the identifier for that sequence file is
		String location = r.getHeader(HttpHeaders.LOCATION);
		String identifier = location.substring(location.lastIndexOf('/') + 1);
		Map<String, String> existingSequenceFile = new HashMap<>();
		existingSequenceFile.put("sequenceFileId", identifier);

		// now figure out where to post the sequence file to add it to the
		// sample
		String sampleBody = asUser().expect().statusCode(HttpStatus.OK.value()).when().get(sampleUri).getBody().asString();
		sequenceFileUri = from(sampleBody).getString("resource.links.find{it.rel == 'sample/sequenceFiles'}.href");

		// add the sequence file to the sample
		r = asUser().given().body(existingSequenceFile).expect().statusCode(HttpStatus.CREATED.value()).when()
				.post(sequenceFileUri);
		location = r.getHeader(HttpHeaders.LOCATION);

		assertNotNull(location);
		assertTrue(location.matches(ITestSystemProperties.BASE_URL + "/api/projects/[0-9]+/samples/[0-9]+/sequenceFiles/[0-9]+"));

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
	public void testAddSequenceFilePair() {
		String filePairURI = ITestSystemProperties.BASE_URL + "/api/projects/5/samples/1/sequenceFiles/pair";

		String file1URI = ITestSystemProperties.BASE_URL + "/api/projects/5/samples/1/sequenceFiles/1";

		List<Long> files = Lists.newArrayList(1l,2l);

		asAdmin().body(files).expect().response().statusCode(HttpStatus.CREATED.value()).when().post(filePairURI);

		asAdmin().expect().body("resource.links.rel", hasItems(RESTSampleSequenceFilesController.REL_PAIR)).when()
				.get(file1URI);
	}
}
