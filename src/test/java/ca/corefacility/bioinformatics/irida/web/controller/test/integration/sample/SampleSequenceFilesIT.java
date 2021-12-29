package ca.corefacility.bioinformatics.irida.web.controller.test.integration.sample;

import static ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils.asAdmin;
import static ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestAuthUtils.asUser;
import static io.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.net.HttpHeaders;
import io.restassured.response.Response;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaDbUnitConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.web.controller.api.samples.RESTSampleSequenceFilesController;
import ca.corefacility.bioinformatics.irida.web.controller.test.integration.util.ITestSystemProperties;

/**
 * Integration tests for working with sequence files and samples.
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ImportAutoConfiguration({
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class,
})
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiJdbcDataSourceConfig.class,
		IridaApiPropertyPlaceholderConfig.class, IridaDbUnitConfig.class }, initializers = ConfigDataApplicationContextInitializer.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/web/controller/test/integration/sample/SampleSequenceFilesIntegrationTest.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class SampleSequenceFilesIT {

	private static final byte[] FASTQ_FILE_CONTENTS = "@testread\nACGTACGT\n+\n????????".getBytes();

	@Test
	public void testAddSequenceFileToSample() throws IOException, InterruptedException {
		String sampleUri = ITestSystemProperties.BASE_URL + "/api/projects/5/samples/1";
		Response response = asUser().expect().statusCode(HttpStatus.OK.value()).when().get(sampleUri);
		String sampleBody = response.getBody().asString();
		String sequenceFileUri = from(sampleBody)
				.getString("resource.links.find{it.rel == 'sample/sequenceFiles'}.href");
		String unpairedUri = from(sampleBody)
				.getString("resource.links.find{it.rel == 'sample/sequenceFiles/unpaired'}.href");

		// prepare a file for sending to the server
		Path sequenceFile = Files.createTempFile(null, null);
		Files.write(sequenceFile, FASTQ_FILE_CONTENTS);

		Map<String, String> fileParams = new HashMap<>();
		fileParams.put("description", "some file");

		Response r = asAdmin().given().contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
				.multiPart("file", sequenceFile.toFile())
				.multiPart("parameters", fileParams, MediaType.APPLICATION_JSON_VALUE).expect()
				.statusCode(HttpStatus.CREATED.value()).when().post(sequenceFileUri);

		// check that the location and link headers were created:
		String location = r.getHeader(HttpHeaders.LOCATION);
		assertNotNull("location must exist", location);
		assertTrue("location must be correct", location.matches(unpairedUri + "/[0-9]+/files/[0-9]+"));
		// confirm that the sequence file was added to the sample sequence files
		// list
		asUser().expect().body("resource.resources.fileName", hasItem(sequenceFile.getFileName().toString())).and()
				.body("resource.resources.links[0].rel", hasItems("self")).when().get(sequenceFileUri);

		String qcPath = asUser().expect().statusCode(HttpStatus.OK.value()).and()
				.body("resource.links.rel", hasItems("sequencefile/qc")).when().get(location).andReturn().jsonPath()
				.getString("resource.links.find{it.rel == 'sequencefile/qc'}.href");

		// Wait for FASTQC to finish
		Thread.sleep(15000);

		asUser().expect().statusCode(HttpStatus.OK.value()).when().get(qcPath);

		// clean up
		Files.delete(sequenceFile);
	}

	@Test
	public void testAddSequenceFilePairToSample() throws IOException {
		String sampleUri = ITestSystemProperties.BASE_URL + "/api/projects/5/samples/1";
		Response response = asUser().expect().statusCode(HttpStatus.OK.value()).when().get(sampleUri);
		String sampleBody = response.getBody().asString();
		String sequenceFilePairUri = from(sampleBody).getString(
				"resource.links.find{it.rel == 'sample/sequenceFiles/pairs'}.href");

		Path sequenceFile1 = Files.createTempFile("File1_R1_001", ".fastq");
		Path sequenceFile2 = Files.createTempFile("File1_R2_001", ".fastq");
		Files.write(sequenceFile1, FASTQ_FILE_CONTENTS);
		Files.write(sequenceFile2, FASTQ_FILE_CONTENTS);

		Map<String, String> fileParams = new HashMap<>();
		fileParams.put("description", "some file");

		Response r = asAdmin().given().contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
				.multiPart("file1", sequenceFile1.toFile())
				.multiPart("parameters1", fileParams, MediaType.APPLICATION_JSON_VALUE)
				.multiPart("file2", sequenceFile2.toFile())
				.multiPart("parameters2", fileParams, MediaType.APPLICATION_JSON_VALUE).expect()
				.statusCode(HttpStatus.CREATED.value()).when().post(sequenceFilePairUri);

		String location = r.body().jsonPath().get("resource.links.find{it.rel == 'self'}.href");

		assertTrue("Response body must contain self rel", location.matches(sequenceFilePairUri + "/[0-9]+"));

		// confirm the resource exist
		asUser().expect().body("resource.links.rel", hasItem("self")).when().get(location);

		// clean up
		Files.delete(sequenceFile1);
		Files.delete(sequenceFile2);
	}

	@Test
	public void testAddSequenceFileToSampleWithOptionalProperties() throws IOException {
		String sampleUri = ITestSystemProperties.BASE_URL + "/api/projects/5/samples/1";
		Response response = asUser().expect().statusCode(HttpStatus.OK.value()).when().get(sampleUri);
		String sampleBody = response.getBody().asString();
		String sequenceFileUri = from(sampleBody).getString(
				"resource.links.find{it.rel == 'sample/sequenceFiles'}.href");
		String unpairedUri = from(sampleBody).getString(
				"resource.links.find{it.rel == 'sample/sequenceFiles/unpaired'}.href");

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
		assertTrue("location must be correct", location.matches(unpairedUri + "/[0-9]+/files/[0-9]+"));

		// confirm that the sequence file contains the given optional property
		asAdmin().expect().body("resource.optionalProperty", equalTo(optionalValue)).when().get(location);

		// clean up
		Files.delete(sequenceFile);
	}

	@Test
	public void testRemoveSequenceFileFromSample() throws IOException {
		// for now, add a sequence file to the sample so that we can remove it
		String projectSampleUri = ITestSystemProperties.BASE_URL + "/api/projects/5/samples/1";
		String sampleUri = ITestSystemProperties.BASE_URL + "/api/samples/1";
		Response response = asUser().expect().statusCode(HttpStatus.OK.value()).when().get(projectSampleUri);
		String sampleBody = response.getBody().asString();
		String sequenceFileUri = from(sampleBody).getString(
				"resource.links.find{it.rel == 'sample/sequenceFiles'}.href");
		// prepare a file for sending to the server
		Path sequenceFile = Files.createTempFile(null, null);
		Files.write(sequenceFile, FASTQ_FILE_CONTENTS);

		Response r = asAdmin().given().contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
				.multiPart("file", sequenceFile.toFile()).expect().statusCode(HttpStatus.CREATED.value()).when()
				.post(sequenceFileUri);

		String seqObjectLocation = from(r.getBody().asString()).getString(
				"resource.links.find{it.rel == 'sequenceFile/sequencingObject'}.href");

		r = asAdmin().expect().statusCode(HttpStatus.OK.value()).when().delete(seqObjectLocation);
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

	@Test
	public void testReadPairedSequenceFiles() {
		String pairsRel = RESTSampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILE_PAIRS;

		String sampleUri = ITestSystemProperties.BASE_URL + "/api/projects/5/samples/1";
		Response response = asUser().expect().statusCode(HttpStatus.OK.value()).when().get(sampleUri);
		String sampleBody = response.getBody().asString();

		String sequenceFilePairsUri = from(sampleBody).getString(
				"resource.links.find{it.rel == '" + pairsRel + "'}.href");

		asUser().expect().statusCode(HttpStatus.OK.value()).and().body("resource.resources[0].files", hasSize(2))
				.when().get(sequenceFilePairsUri);
	}

	@Test
	public void testReadForwardReverseFromPair() {
		String sequenceFilePairUri = ITestSystemProperties.BASE_URL + "/api/samples/1/pairs/1";

		Response response = asUser().expect().statusCode(HttpStatus.OK.value()).when().get(sequenceFilePairUri);
		String forwardLink = response.jsonPath().getString(
				"resource.links.find{it.rel=='" + RESTSampleSequenceFilesController.REL_PAIR_FORWARD + "'}.href");
		String reverseLink = response.jsonPath().getString(
				"resource.links.find{it.rel=='" + RESTSampleSequenceFilesController.REL_PAIR_REVERSE + "'}.href");

		asUser().expect().statusCode(HttpStatus.OK.value()).and()
				.body("resource.fileName", equalTo("sequenceFile2_01_L001_R1_001.fastq.gz")).when().get(forwardLink);
		asUser().expect().statusCode(HttpStatus.OK.value()).and()
				.body("resource.fileName", equalTo("sequenceFile2_01_L001_R2_001.fastq.gz")).when().get(reverseLink);
	}
	
	@Test
	public void testReadSequenceFilesNoAnalysis() {
		String sequenceFilePairUri = ITestSystemProperties.BASE_URL + "/api/samples/1/pairs/4";

		asUser().get(sequenceFilePairUri).then().statusCode(HttpStatus.OK.value()).and()
				.body("resource.links.rel", not(anyOf(hasItem("analysis/assembly"), hasItem("analysis/sistr"))));
	}

	@Test
	public void testReadSequenceFilesAssemblyAnalysis() {
		String sequenceFilePairUri = ITestSystemProperties.BASE_URL + "/api/samples/1/pairs/2";

		asUser().get(sequenceFilePairUri).then().statusCode(HttpStatus.OK.value()).and().body("resource.links.rel",
				both(hasItem("analysis/assembly")).and(not(hasItem("analysis/sistr"))));
	}

	@Test
	public void testReadSequenceFilesSISTRAnalysis() {
		String sequenceFilePairUri = ITestSystemProperties.BASE_URL + "/api/samples/1/pairs/3";

		asUser().get(sequenceFilePairUri).then().statusCode(HttpStatus.OK.value()).and().body("resource.links.rel",
				both(hasItem("analysis/sistr")).and(not(hasItem("analysis/assembly"))));
	}
	
	@Test
	public void testReadSequenceFilesSISTRAssemblyAnalysis() {
		String sequenceFilePairUri = ITestSystemProperties.BASE_URL + "/api/samples/1/pairs/1";

		asUser().get(sequenceFilePairUri).then().statusCode(HttpStatus.OK.value()).and().body("resource.links.rel",
				both(hasItem("analysis/sistr")).and(hasItem("analysis/assembly")));
	}

	@Test
	public void testReadUnPairedSequenceFiles() {
		String unpairedRel = RESTSampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILE_UNPAIRED;

		String sampleUri = ITestSystemProperties.BASE_URL + "/api/projects/5/samples/1";
		Response response = asUser().expect().statusCode(HttpStatus.OK.value()).when().get(sampleUri);
		String sampleBody = response.getBody().asString();
		String sequenceFilePairsUri = from(sampleBody).getString(
				"resource.links.find{it.rel == '" + unpairedRel + "'}.href");

		asUser().expect().statusCode(HttpStatus.OK.value()).and().body("resource.resources.files", hasSize(1)).when()
				.get(sequenceFilePairsUri);
	}

}
