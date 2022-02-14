package ca.corefacility.bioinformatics.irida.web.controller.test.integration.security;

import static io.restassured.RestAssured.expect;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.annotation.RestIntegrationTest;

/**
 * General tests relating to security for the REST API.
 * 
 */
@RestIntegrationTest
public class SecurityIT {

	/**
	 * Test that we get the right type of response when we don't have valid
	 * credentials.
	 */
	@Test
	public void testAccessWithoutAuthentication() {
		expect().body("error", is("invalid_request")).and()
				.body("error_description", containsString("No client credentials were provided"))
				.statusCode(HttpStatus.SC_UNAUTHORIZED).when().get("/api");
	}
}
