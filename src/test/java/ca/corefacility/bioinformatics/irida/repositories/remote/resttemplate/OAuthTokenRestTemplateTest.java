package ca.corefacility.bioinformatics.irida.repositories.remote.resttemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaOAuthException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.RemoteAPIToken;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;

/**
 * Unit tests for OAuthTokenRestTemplate
 * 
 *
 */
public class OAuthTokenRestTemplateTest {

	private RemoteAPITokenService tokenService;
	private OAuthTokenRestTemplate restTemplate;
	private RemoteAPI remoteAPI;
	private URI serviceURI;

	@BeforeEach
	public void setUp() throws URISyntaxException {
		tokenService = mock(RemoteAPITokenService.class);
		serviceURI = new URI("http://uri");
		remoteAPI = new RemoteAPI("service name", serviceURI.toString(), "a service", "clientId", "clientSecret");
		restTemplate = new OAuthTokenRestTemplate(tokenService, remoteAPI);
	}

	@Test
	public void testCreateRequest() throws URISyntaxException, IOException {
		String tokenString = "token111111";
		RemoteAPIToken token = new RemoteAPIToken(tokenString, remoteAPI, new Date(System.currentTimeMillis() + 10000));
		when(tokenService.getToken(remoteAPI)).thenReturn(token);

		ClientHttpRequest createRequest = restTemplate.createRequest(serviceURI, HttpMethod.GET);

		verify(tokenService).getToken(remoteAPI);
		assertNotNull(createRequest);
		assertTrue(createRequest.getHeaders().containsKey("Authorization"));

		List<String> list = createRequest.getHeaders().get("Authorization");
		assertTrue(list.contains("Bearer " + tokenString));
	}

	@Test
	public void testCreateRequestExpiredToken() throws URISyntaxException, IOException {
		String tokenString = "token111111";
		RemoteAPIToken token = new RemoteAPIToken(tokenString, remoteAPI, new Date(System.currentTimeMillis() - 10000));
		when(tokenService.getToken(remoteAPI)).thenReturn(token);

		assertThrows(IridaOAuthException.class, () -> {
			restTemplate.createRequest(serviceURI, HttpMethod.GET);
		});
	}

	@Test
	public void testCreateRequestNoToken() throws URISyntaxException, IOException {
		when(tokenService.getToken(remoteAPI)).thenThrow(new EntityNotFoundException("no token for this service"));

		assertThrows(IridaOAuthException.class, () -> {
			restTemplate.createRequest(serviceURI, HttpMethod.GET);
		});
	}
}
