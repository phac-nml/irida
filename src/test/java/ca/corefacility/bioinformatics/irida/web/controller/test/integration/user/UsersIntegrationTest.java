package ca.corefacility.bioinformatics.irida.web.controller.test.integration.user;

import org.junit.Test;

import static com.jayway.restassured.RestAssured.expect;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;

/**
 * Integration tests for users.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class UsersIntegrationTest {

    @Test
    public void testGetAllUsers() {
        expect().body("resource.links.rel", hasItems("self", "users/pages/first")).and()
                .body("resource.resources.username", hasItem("fbristow")).when().get("/users/all");
    }
}
