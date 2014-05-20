package ca.corefacility.bioinformatics.irida.repositories.remote.resttemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaOAuthException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.RemoteAPIToken;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;

public class OAuthTokenRestTemplateIT {
	private RemoteAPITokenService tokenService;
	private OAuthTokenRestTemplate restTemplate;
	private RemoteAPI remoteAPI;
	private URI serviceURI;
	private MockRestServiceServer mockServer;

	@Before
	public void setUp() throws URISyntaxException {
		tokenService = mock(RemoteAPITokenService.class);
		serviceURI = new URI("http://irida.ca/service");
		remoteAPI = new RemoteAPI(serviceURI.toString(), "a service", "clientId", "clientSecret");
		restTemplate = new OAuthTokenRestTemplate(tokenService, remoteAPI);

		mockServer = MockRestServiceServer.createServer(restTemplate);
	}

	@Test
	public void testRequest() {
		String tokenString = "token111111";
		String responseMessage = "{ \"message\" : \"all good\" }";
		RemoteAPIToken token = new RemoteAPIToken(tokenString, remoteAPI, new Date(System.currentTimeMillis() + 10000));
		when(tokenService.getToken(remoteAPI)).thenReturn(token);

		mockServer.expect(requestTo(serviceURI)).andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess(responseMessage, MediaType.APPLICATION_JSON));

		ResponseEntity<String> forEntity = restTemplate.getForEntity(serviceURI, String.class);
		assertNotNull(forEntity);
		assertEquals(responseMessage, forEntity.getBody());
		mockServer.verify();
	}

	@Test(expected = IridaOAuthException.class)
	public void testRequestExpiredToken() {
		String tokenString = "token111111";
		RemoteAPIToken token = new RemoteAPIToken(tokenString, remoteAPI, new Date(System.currentTimeMillis() - 10000));
		when(tokenService.getToken(remoteAPI)).thenReturn(token);

		restTemplate.getForEntity(serviceURI, String.class);
	}
	
	@Test(expected = IridaOAuthException.class)
	public void testRequestNoToken() {
		when(tokenService.getToken(remoteAPI)).thenThrow(new EntityNotFoundException("no token for this service"));

		restTemplate.getForEntity(serviceURI, String.class);
	}
	
	@Test(expected = IridaOAuthException.class)
	public void testBadToken(){
		String tokenString = "token111111";
		RemoteAPIToken token = new RemoteAPIToken(tokenString, remoteAPI, new Date(System.currentTimeMillis() + 10000));
		when(tokenService.getToken(remoteAPI)).thenReturn(token);

		mockServer.expect(requestTo(serviceURI)).andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.UNAUTHORIZED));

		restTemplate.getForEntity(serviceURI, String.class);
	}
}
