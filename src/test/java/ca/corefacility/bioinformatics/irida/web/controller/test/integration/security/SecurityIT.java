package ca.corefacility.bioinformatics.irida.web.controller.test.integration.security;

import static io.restassured.RestAssured.expect;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import ca.corefacility.bioinformatics.irida.config.IridaIntegrationTestUriConfig;

/**
 * General tests relating to security for the REST API.
 *
 */
@Tag("IntegrationTest") @Tag("Rest")
@ActiveProfiles("it")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(IridaIntegrationTestUriConfig.class)
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