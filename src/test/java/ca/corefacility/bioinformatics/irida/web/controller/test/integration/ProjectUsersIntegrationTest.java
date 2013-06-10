package ca.corefacility.bioinformatics.irida.web.controller.test.integration;

import com.google.common.net.HttpHeaders;
import com.jayway.restassured.response.Response;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.path.json.JsonPath.from;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.*;

/**
 * Integration test for project and user.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class ProjectUsersIntegrationTest {
    @Test
    public void testAddExistingUserToProject() {
        String username = "aaron";
        String name = "Aaron Petkau";
        String projectUri = "http://localhost:8080/api/projects/6b80820f-38f8-4c73-83a6-12d17dc2c31c";
        Map<String, String> users = new HashMap<>();
        users.put("userId", username);

        // get the project
        String projectJson = get(projectUri).asString();
        // get the uri for adding users to the project
        String usersUri = from(projectJson).get("resource.links.find{it.rel == 'project/users'}.href");

        // post the users uri to add aaron to the project
        Response r = given().body(users).expect().statusCode(HttpStatus.CREATED.value()).when().post(usersUri);

        // check that the locations make sense
        String location = r.getHeader(HttpHeaders.LOCATION);

        assertNotNull(location);
        assertEquals(projectUri + "/users/" + username, location);

        // confirm that aaron is part of the project now
        expect().body("relatedResources.users.resources.label", hasItem(name)).when().get(projectUri);
    }
}
