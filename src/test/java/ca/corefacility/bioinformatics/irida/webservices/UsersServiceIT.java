package ca.corefacility.bioinformatics.irida.webservices;

import com.jayway.restassured.RestAssured;
import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import org.junit.Before;
import org.junit.Test;

public class UsersServiceIT {

    @Before
    public void setUp() {
        RestAssured.authentication = RestAssured.basic("fbristow", "password1");
    }

    @Test
    public void testGetUsers() {
        expect().body("id", hasItems(0, 1)).when().get("/api/users");
    }

    @Test
    public void testNoPasswords() {
        expect().body("password", hasItems(nullValue(), nullValue())).when().get("/api/users");
        // asking for a field that doesn't exist returns null.
    }
}
