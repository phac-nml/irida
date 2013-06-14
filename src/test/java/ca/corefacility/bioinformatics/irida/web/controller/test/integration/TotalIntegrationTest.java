package ca.corefacility.bioinformatics.irida.web.controller.test.integration;

import com.google.common.net.HttpHeaders;
import com.jayway.restassured.response.Response;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.path.json.JsonPath.from;


/**
 * This integration test pushes through a complete set of actions on a project, sample and sequence file from
 * creation to deletion.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class TotalIntegrationTest {
    @Test
    public void testTotal() throws IOException {
        Map<String, String> project = new HashMap<>();
        project.put("name", "Completely new project.");
        // start by creating a project
        Response r = given().body(project).expect().statusCode(HttpStatus.CREATED.value()).when().post("/projects");

        // figure out where we're supposed to go to add new samples to the project
        String projectLocation = r.getHeader(HttpHeaders.LOCATION);
        String projectJson = get(projectLocation).asString();
        String samplesLocation = from(projectJson).getString("resource.links.find{it.rel == 'project/samples'}.href");
        String sequenceFilesLocation = from(projectJson).getString("resource.links.find{it.rel == 'project/sequenceFiles'}.href");

        // add a new sequence file to the project
        Path sequenceFile = Files.createTempFile(null, null);
        Files.write(sequenceFile, ">test read\nACGTACTCATG".getBytes());
        r = given().contentType(MediaType.MULTIPART_FORM_DATA_VALUE).multiPart("file", sequenceFile.toFile())
                .expect().statusCode(HttpStatus.CREATED.value()).when().post(sequenceFilesLocation);
        // get the location and the identifier of the sequence file
        String sequenceFileLocation = r.getHeader(HttpHeaders.LOCATION);
        // get the sequence file location
        String sequenceFileIdentifier = get(sequenceFileLocation).jsonPath().getString("resource.identifier");

        // add a new sample to the project
        Map<String, String> sample = new HashMap<>();
        sample.put("sampleName", "awww yiss");
        r = given().body(sample).expect().statusCode(HttpStatus.CREATED.value()).when().post(samplesLocation);

        // get the location of the individual sample
        String sampleLocation = r.getHeader(HttpHeaders.LOCATION);

        // now get the sample
        String sampleJson = get(sampleLocation).asString();
        String sampleSequenceFilesLocation = from(sampleJson).getString("resource.links.find{it.rel == 'sample/sequenceFiles'}.href");

        // add a new sequence file to the sample
        r = given().contentType(MediaType.MULTIPART_FORM_DATA_VALUE).multiPart("file", sequenceFile.toFile())
                .expect().statusCode(HttpStatus.CREATED.value()).when().post(sampleSequenceFilesLocation);
        String sampleSequenceFileLocation = r.getHeader(HttpHeaders.LINK);
        sampleSequenceFileLocation = sampleSequenceFileLocation.substring(1, sampleSequenceFileLocation.indexOf('>'));

        // add an existing sequence file to the sample
        Map<String, String> existingSequenceFile = new HashMap<>();
        existingSequenceFile.put("sequenceFileId", sequenceFileIdentifier);
        r = given().body(existingSequenceFile).expect().statusCode(HttpStatus.CREATED.value()).when().post(sampleSequenceFilesLocation);

    }
}
