package ca.corefacility.bioinformatics.irida.web.controller.test.integration.user;

import org.junit.Test;

import static com.jayway.restassured.RestAssured.expect;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;

/**
 * Integration tests for users.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class UsersIntegrationTest {

    @Test
    public void testGetAllUsers() {
        expect().body("resource.links.rel", hasItems("self", "collection/pages/first")).and()
                .body("resource.resources.username", hasItem("fbristow")).when().get("/users/all");
    }
    
    @Test
    public void testGetCurrentUser() {
    	expect().body("resource.links.rel", hasItems("self", "user/projects")).and()
    			.body("resource.username", is("fbristow")).when().get("/users/current");
    }
}
