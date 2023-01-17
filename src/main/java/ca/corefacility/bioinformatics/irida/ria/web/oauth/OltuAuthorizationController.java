package ca.corefacility.bioinformatics.irida.ria.web.oauth;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.corefacility.bioinformatics.irida.exceptions.IridaOAuthProblemException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;

import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.openid.connect.sdk.AuthenticationErrorResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponseParser;

/**
 * Controller for handling OAuth2 authorizations
 */
@Controller
public class OltuAuthorizationController {
	private static final Logger logger = LoggerFactory.getLogger(OltuAuthorizationController.class);

	@Value("${server.base.url}")
	private String serverBase;

	public static final String TOKEN_ENDPOINT = "/api/oauth/authorization/token";

	private final RemoteAPITokenService tokenService;
	private final RemoteAPIService remoteAPIService;

	@Autowired
	public OltuAuthorizationController(RemoteAPITokenService tokenService, RemoteAPIService remoteAPIService) {
		this.tokenService = tokenService;
		this.remoteAPIService = remoteAPIService;
	}

	/**
	 * Begin authentication procedure by redirecting to remote authorization location
	 *
	 * @param session   The current {@link HttpSession}
	 * @param remoteAPI The API we need to authenticate with
	 * @param redirect  The location to redirect back to after authentication is complete
	 * @return A ModelAndView beginning the authentication procedure
	 */
	public String authenticate(HttpSession session, RemoteAPI remoteAPI, String redirect) {
		// get the URI for the remote service we'll be requesting from
		String serviceURI = remoteAPI.getServiceURI();

		// build the authorization path
		URI serviceAuthLocation = UriBuilder.fromUri(serviceURI).path("oauth").path("authorize").build();

		logger.debug("Authenticating for service: " + remoteAPI);
		logger.debug("Redirect after authentication: " + redirect);

		// build a redirect URI to redirect to after auth flow is completed
		URI tokenRedirect = buildRedirectURI();

		// build state object which is used to extract the authCode to the correct remoteAPI
		String stateUuid = UUID.randomUUID().toString();
		Map<String, String> stateMap = new HashMap<String, String>();
		stateMap.put("apiId", remoteAPI.getId().toString());
		stateMap.put("redirect", redirect);
		session.setAttribute(stateUuid, stateMap);

		// build the redirect query to request an authorization code from the remote API
		AuthorizationRequest request = new AuthorizationRequest.Builder(new ResponseType(ResponseType.Value.CODE),
				new ClientID(remoteAPI.getClientId())).scope(new Scope("read"))
						.state(new State(stateUuid))
						.redirectionURI(tokenRedirect)
						.endpointURI(serviceAuthLocation)
						.build();

		String locURI = request.toURI().toString();
		logger.trace("Authorization request location: " + locURI);

		return "redirect:" + locURI;
	}

	/**
	 * Receive the OAuth2 authorization code and request an OAuth2 token
	 *
	 * @param request  The incoming request
	 * @param response The response to redirect
	 * @param state    The state param which contains a map including apiId and redirect
	 * @return A ModelAndView redirecting back to the resource that was requested
	 * @throws IridaOAuthProblemException
	 * @throws ParseException
	 */
	@RequestMapping(TOKEN_ENDPOINT)
	public String getTokenFromAuthCode(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("state") String state) throws IridaOAuthProblemException, ParseException {
		HttpSession session = request.getSession();

		// Get the OAuth2 auth code
		AuthenticationResponse authResponse = AuthenticationResponseParser
				.parse(new ServletServerHttpRequest(request).getURI());

		if (authResponse instanceof AuthenticationErrorResponse) {
			logger.trace("Unexpected authentication response");
			throw new IridaOAuthProblemException(authResponse.toErrorResponse().getErrorObject().toString());
		}

		// Verify existence of state
		if (authResponse.getState() == null) {
			logger.trace("Authentication response did not contain a state");
			throw new IridaOAuthProblemException("State missing from authentication response");
		} else if (session.getAttribute(authResponse.getState().toString()) == null) {
			logger.trace("State not present in session");
			throw new IridaOAuthProblemException("State not present in session");
		}

		AuthorizationCode code = authResponse.toSuccessResponse().getAuthorizationCode();
		logger.trace("Received auth code: " + code.getValue());

		Map<String, String> stateMap = (Map<String, String>) session.getAttribute(state);

		Long apiId = Long.parseLong(stateMap.get("apiId"));
		String redirect = stateMap.get("redirect");

		// Build the redirect URI to request a token from
		URI tokenRedirect = buildRedirectURI();

		// Read the RemoteAPI from the RemoteAPIService and get the base URI
		RemoteAPI remoteAPI = remoteAPIService.read(apiId);

		tokenService.createTokenFromAuthCode(code, remoteAPI, tokenRedirect);

		// redirect the response back to the requested resource
		return "redirect:" + redirect;
	}

	/**
	 * Build the redirect URI for the token page with the API and resource page
	 *
	 * @return the redirect uri
	 */
	private URI buildRedirectURI() {

		URI redirectURI = UriBuilder.fromUri(serverBase).path(TOKEN_ENDPOINT).build();

		return redirectURI;
	}

	/**
	 * Set the base URL of this server
	 *
	 * @param serverBase the base url of the server.
	 */
	public void setServerBase(String serverBase) {
		this.serverBase = serverBase;
	}
}
