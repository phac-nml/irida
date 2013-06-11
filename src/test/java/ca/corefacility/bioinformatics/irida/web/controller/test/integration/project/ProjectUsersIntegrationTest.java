package ca.corefacility.bioinformatics.irida.web.controller.test.integration.project;

import com.google.common.net.HttpHeaders;
import com.jayway.restassured.response.Response;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static com.jayway.restassured.RestAssured.*;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
        String projectUri = "http://localhost:8080/api/projects/90dd11ef-6fea-4d37-ab79-58b285ca2991";
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

    @Test
    public void testRemoveUserFromProject() {
        String name = "Tom Matthews";
        String projectUri = "http://localhost:8080/api/projects/a77a9cc7-545a-4d5b-8f21-39a449a81bc9";

        // get the project
        String projectJson = get(projectUri).asString();
        String userRelationshipUri = from(projectJson).get("relatedResources.users.resources.find{it.label == '"
                + name + "'}.links.find{it.rel == 'relationship'}.href");

        // delete the user relationship
        expect().body("resource.links.rel", hasItems("project", "project/users"))
                .when().delete(userRelationshipUri);

        // get the project again and confirm that tom isn't part of the project anymore
        expect().body("relatedResources.users.resources.label", not(hasItem(name))).when().get(projectUri);
    }
}
