package ca.corefacility.bioinformatics.irida.webservices;

import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.Matchers.hasItems;
import org.junit.Test;

public class UsersServiceIT {

    @Test
    public void testGetUsers() {
        given().auth().basic("fbristow", "password1").expect().body("id", hasItems(0, 1)).when().get("/api/users");
    }
}
