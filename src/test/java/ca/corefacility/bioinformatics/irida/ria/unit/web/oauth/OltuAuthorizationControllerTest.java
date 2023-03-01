package ca.corefacility.bioinformatics.irida.ria.unit.web.oauth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.ria.web.oauth.OltuAuthorizationController;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;

import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.ParseException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class OltuAuthorizationControllerTest {
	private OltuAuthorizationController controller;
	private RemoteAPIService apiService;
	private RemoteAPITokenService tokenService;
	private HttpSession session;

	private final String serverBase = "http://localserver";

	@BeforeEach
	public void setUp() {
		apiService = mock(RemoteAPIService.class);
		tokenService = mock(RemoteAPITokenService.class);
		session = mock(HttpSession.class);

		controller = new OltuAuthorizationController(tokenService, apiService);
		controller.setServerBase(serverBase);
	}

	@Test
	public void testAuthenticate() throws IOException, UnsupportedEncodingException {
		RemoteAPI remoteAPI = new RemoteAPI("name", "http://uri", "a description", "id", "secret");
		remoteAPI.setId(1L);
		String redirect = "http://base";

		String authenticate = controller.authenticate(session, remoteAPI, redirect);

		// need to decode the escaped characters
		String decoded = URLDecoder.decode(authenticate, "UTF-8");

		assertTrue(decoded.startsWith("redirect:"));
		assertTrue(decoded.contains(serverBase));
	}

	@Test
	public void testGetTokenFromAuthCode() throws IOException, URISyntaxException, ParseException {
		Long apiId = 1L;
		RemoteAPI remoteAPI = new RemoteAPI("name", "http://remoteLocation", "a description", "id", "secret");
		remoteAPI.setId(apiId);
		AuthorizationCode code = new AuthorizationCode("code");
		String redirect = "http://originalPage";

		String stateUuid = UUID.randomUUID().toString();
		Map<String, String> stateMap = new HashMap<String, String>();
		stateMap.put("apiId", apiId.toString());
		stateMap.put("redirect", redirect);

		when(session.getAttribute(stateUuid)).thenReturn(stateMap);

		when(apiService.read(apiId)).thenReturn(remoteAPI);

		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		StringBuffer url = new StringBuffer(serverBase + OltuAuthorizationController.TOKEN_ENDPOINT);
		String query = "code=" + code.getValue() + "&state=" + stateUuid;

		when(request.getRequestURL()).thenReturn(url);
		when(request.getQueryString()).thenReturn(query);
		when(request.getSession()).thenReturn(session);

		controller.getTokenFromAuthCode(request, response, stateUuid);

		verify(apiService).read(apiId);

		ArgumentCaptor<URI> redirectArg = ArgumentCaptor.forClass(URI.class);
		verify(tokenService).createTokenFromAuthCode(eq(code), eq(remoteAPI), redirectArg.capture());

		String capturedRedirect = redirectArg.getValue().toString();
		assertTrue(capturedRedirect.contains(serverBase));
	}

	@Test
	public void testGetTokenFromAuthCodeExtraSlash() throws IOException, URISyntaxException, ParseException {
		Long apiId = 1L;
		RemoteAPI remoteAPI = new RemoteAPI("name", "http://remoteLocation", "a description", "id", "secret");
		remoteAPI.setId(apiId);
		AuthorizationCode code = new AuthorizationCode("code");
		String redirect = "http://originalPage";

		String stateUuid = UUID.randomUUID().toString();
		Map<String, String> stateMap = new HashMap<String, String>();
		stateMap.put("apiId", apiId.toString());
		stateMap.put("redirect", redirect);

		when(session.getAttribute(stateUuid)).thenReturn(stateMap);

		//adding a trailing slash to the serverbase to try to confuse the redirect URI
		controller.setServerBase(serverBase + "/");

		when(apiService.read(apiId)).thenReturn(remoteAPI);

		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		StringBuffer url = new StringBuffer(serverBase + "/" + OltuAuthorizationController.TOKEN_ENDPOINT);
		String query = "code=" + code.getValue() + "&state=" + stateUuid;

		when(request.getRequestURL()).thenReturn(url);
		when(request.getQueryString()).thenReturn(query);
		when(request.getSession()).thenReturn(session);

		controller.getTokenFromAuthCode(request, response, stateUuid);

		verify(apiService).read(apiId);

		ArgumentCaptor<URI> redirectArg = ArgumentCaptor.forClass(URI.class);
		verify(tokenService).createTokenFromAuthCode(eq(code), eq(remoteAPI), redirectArg.capture());

		String capturedRedirect = redirectArg.getValue().toString();
		assertTrue(capturedRedirect.contains(serverBase));
		assertFalse(capturedRedirect.contains(serverBase + "//"));
	}

	@Test
	public void testGetTokenFromAuthCodeError() throws IOException, URISyntaxException, ParseException {
		Long apiId = 1L;
		RemoteAPI remoteAPI = new RemoteAPI("name", "http://remoteLocation", "a description", "id", "secret");
		remoteAPI.setId(apiId);
		AuthorizationCode code = new AuthorizationCode("code");
		String redirect = "http://originalPage";

		String stateUuid = UUID.randomUUID().toString();
		Map<String, String> stateMap = new HashMap<String, String>();
		stateMap.put("apiId", apiId.toString());
		stateMap.put("redirect", redirect);

		when(session.getAttribute(stateUuid)).thenReturn(stateMap);

		when(apiService.read(apiId)).thenReturn(remoteAPI);

		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		StringBuffer url = new StringBuffer(serverBase + OltuAuthorizationController.TOKEN_ENDPOINT);

		when(request.getRequestURL()).thenReturn(url);
		when(request.getQueryString()).thenReturn("");
		when(request.getSession()).thenReturn(session);

		assertThrows(ParseException.class, () -> {
			controller.getTokenFromAuthCode(request, response, stateUuid);
		});
	}
}
