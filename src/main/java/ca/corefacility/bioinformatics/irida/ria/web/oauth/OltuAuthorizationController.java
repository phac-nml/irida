package ca.corefacility.bioinformatics.irida.ria.web.oauth;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.UriBuilder;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAuthzResponse;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.RemoteAPIToken;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;

/**
 * Controller for handling OAuth2 authorizations
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
@Controller
public class OltuAuthorizationController {
	private static final Logger logger = LoggerFactory.getLogger(OltuAuthorizationController.class);

	private static final long ONE_SECOND_IN_MS = 1000;

	@Value("${server.base.url}")
	private String serverBase;

	public static final String TOKEN_ENDPOINT = "/authorization/token";

	private final RemoteAPITokenService tokenService;
	private final RemoteAPIService remoteAPIService;
	private final OAuthClient oauthClient;

	@Autowired
	public OltuAuthorizationController(RemoteAPITokenService tokenService, RemoteAPIService remoteAPIService,
			OAuthClient oauthClient) {
		this.tokenService = tokenService;
		this.remoteAPIService = remoteAPIService;
		this.oauthClient = oauthClient;
	}

	/**
	 * Begin authentication procedure by redirecting to remote authorization
	 * location
	 * 
	 * @param remoteAPI
	 *            The API we need to authenticate with
	 * @param redirect
	 *            The location to redirect back to after authentication is
	 *            complete
	 * @return A ModelAndView beginning the authentication procedure
	 * @throws OAuthSystemException
	 */
	public String authenticate(RemoteAPI remoteAPI, String redirect) throws OAuthSystemException {
		// get the URI for the remote service we'll be requesting from
		String serviceURI = remoteAPI.getServiceURI();

		// build the authorization path
		URI serviceAuthLocation = UriBuilder.fromUri(serviceURI).path("oauth").path("authorize").build();

		logger.debug("Authenticating for service: " + remoteAPI);
		logger.debug("Redirect after authentication: " + redirect);

		// build a redirect URI to redirect to after auth flow is completed
		String tokenRedirect = buildRedirectURI(remoteAPI.getId(), redirect);

		// build the redirect query to request an authorization code from the
		// remote API
		OAuthClientRequest request = OAuthClientRequest.authorizationLocation(serviceAuthLocation.toString())
				.setClientId(remoteAPI.getClientId()).setRedirectURI(tokenRedirect)
				.setResponseType(ResponseType.CODE.toString()).setScope("read").buildQueryMessage();

		String locURI = request.getLocationUri();
		logger.trace("Authorization request location: " + locURI);

		return "redirect:" + locURI;
	}

	/**
	 * Receive the OAuth2 authorization code and request an OAuth2 token
	 * 
	 * @param request
	 *            The incoming request
	 * @param response
	 *            The response to redirect
	 * @param apiId
	 *            the Long ID of the API we're requesting from
	 * @param redirect
	 *            The URL location to redirect to after completion
	 * @return A ModelAndView redirecting back to the resource that was
	 *         requested
	 * @throws IOException
	 * @throws OAuthSystemException
	 * @throws OAuthProblemException
	 * @throws URISyntaxException
	 */
	@RequestMapping(TOKEN_ENDPOINT)
	public String getTokenFromAuthCode(HttpServletRequest request, HttpServletResponse response, @RequestParam("apiId") Long apiId,
			@RequestParam("redirect") String redirect) throws IOException, OAuthSystemException, OAuthProblemException,
			URISyntaxException {

		// get the current time for the expiry calculation
		Long currentTime = System.currentTimeMillis();

		// Get the OAuth2 auth code
		OAuthAuthzResponse oar = OAuthAuthzResponse.oauthCodeAuthzResponse(request);
		String code = oar.getCode();
		logger.trace("Received auth code: " + code);

		// Read the RemoteAPI from the RemoteAPIService and get the base URI
		RemoteAPI remoteAPI = remoteAPIService.read(apiId);
		String serviceURI = remoteAPI.getServiceURI();

		// Build the token location for this service
		URI serviceTokenLocation = UriBuilder.fromUri(serviceURI).path("oauth").path("token").build();
		logger.debug("Remote token location: " + serviceTokenLocation);

		// Build the redirect URI to request a token from
		String tokenRedirect = buildRedirectURI(apiId, redirect);

		// Create the token request form the given auth code
		OAuthClientRequest tokenRequest = OAuthClientRequest.tokenLocation(serviceTokenLocation.toString())
				.setClientId(remoteAPI.getClientId()).setClientSecret(remoteAPI.getClientSecret())
				.setRedirectURI(tokenRedirect).setCode(code).setGrantType(GrantType.AUTHORIZATION_CODE)
				.buildBodyMessage();

		// execute the request
		OAuthJSONAccessTokenResponse accessTokenResponse = oauthClient.accessToken(tokenRequest);

		// read the response for the access token
		String accessToken = accessTokenResponse.getAccessToken();

		// TODO: Handle Refresh Tokens
		// String refreshToken = accessTokenResponse.getRefreshToken();

		// check the token expiry
		Long expiresIn = accessTokenResponse.getExpiresIn();
		Date expiry = new Date(currentTime + (expiresIn * ONE_SECOND_IN_MS));
		logger.debug("Token expiry: " + expiry);

		// create the OAuth2 token and store it
		RemoteAPIToken token = new RemoteAPIToken(accessToken, remoteAPI, expiry);
		tokenService.create(token);

		// redirect the response back to the requested resource
		return "redirect:" + redirect;
	}

	/**
	 * Build the redirect URI for the token page with the API and resource page
	 * 
	 * @param apiId
	 *            the Long ID of the API to request from
	 * @param redirectPage
	 *            the resource page to redirect to once the authorizatino is
	 *            complete
	 * @return
	 */
	private String buildRedirectURI(Long apiId, String redirectPage) {

		URI build = UriBuilder.fromUri(serverBase + TOKEN_ENDPOINT).queryParam("apiId", apiId)
				.queryParam("redirect", redirectPage).build();
		return build.toString();
	}

	/**
	 * Set the base URL of this server
	 * 
	 * @param serverBase
	 */
	public void setServerBase(String serverBase) {
		this.serverBase = serverBase;
	}
}
