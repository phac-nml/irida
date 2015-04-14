package ca.corefacility.bioinformatics.irida.ria.unit.web.oauth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.RemoteAPIToken;
import ca.corefacility.bioinformatics.irida.ria.web.oauth.OltuAuthorizationController;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;

public class OltuAuthorizationControllerTest {
	private OltuAuthorizationController controller;

	private RemoteAPIService apiService;

	private RemoteAPITokenService tokenService;

	OAuthClient oauthClient;

	private final String serverBase = "http://localserver";

	@Before
	public void setUp() {
		apiService = mock(RemoteAPIService.class);
		tokenService = mock(RemoteAPITokenService.class);
		oauthClient = mock(OAuthClient.class);

		controller = new OltuAuthorizationController(tokenService, apiService, oauthClient);
		controller.setServerBase(serverBase);
	}

	@Test
	public void testAuthenticate() throws OAuthSystemException, UnsupportedEncodingException {
		RemoteAPI remoteAPI = new RemoteAPI("name", "http://uri", "a description", "id", "secret");
		remoteAPI.setId(1L);
		String redirect = "http://base";

		String authenticate = controller.authenticate(remoteAPI, redirect);

		// need to decode the escaped characters
		String decoded = URLDecoder.decode(authenticate, "UTF-8");

		assertTrue(decoded.startsWith("redirect:"));
		assertTrue(decoded.contains(redirect));
		assertTrue(decoded.contains(serverBase));
	}

	@Test
	public void testGetTokenFromAuthCode() throws IOException, OAuthSystemException, OAuthProblemException,
			URISyntaxException {
		Long apiId = 1L;
		RemoteAPI remoteAPI = new RemoteAPI("name", "http://remoteLocation", "a description", "id", "secret");
		remoteAPI.setId(apiId);

		when(apiService.read(apiId)).thenReturn(remoteAPI);

		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		Map<String, String[]> requestParams = new HashMap<>();
		requestParams.put("code", new String[] { "code" });

		when(request.getParameterMap()).thenReturn(requestParams);

		String tokenString = "abc123";
		OAuthJSONAccessTokenResponse oauthResponse = mock(OAuthJSONAccessTokenResponse.class);
		when(oauthResponse.getAccessToken()).thenReturn(tokenString);
		when(oauthResponse.getExpiresIn()).thenReturn(1L);

		when(oauthClient.accessToken(any(OAuthClientRequest.class))).thenReturn(oauthResponse);

		String redirect = "http://originalPage";

		String responseView = controller.getTokenFromAuthCode(request, response, apiId, redirect);

		verify(apiService).read(apiId);

		ArgumentCaptor<RemoteAPIToken> tokenArgument = ArgumentCaptor.forClass(RemoteAPIToken.class);
		verify(tokenService).create(tokenArgument.capture());

		RemoteAPIToken createdToken = tokenArgument.getValue();
		assertEquals(tokenString, createdToken.getTokenString());
		assertEquals(remoteAPI, createdToken.getRemoteApi());
		assertEquals("redirect:" + redirect, responseView);

	}
}
