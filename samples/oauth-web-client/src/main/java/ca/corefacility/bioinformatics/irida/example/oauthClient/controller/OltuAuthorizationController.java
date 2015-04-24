package ca.corefacility.bioinformatics.irida.example.oauthClient.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.UriBuilder;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAuthzResponse;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class OltuAuthorizationController {
	private static final Logger logger = LoggerFactory.getLogger(OltuAuthorizationController.class);

	private final String tokenRedirect = "http://localhost:8181/token";

	// Storing the entered credentials.
	private String clientId;
	private String clientSecret;
	private String serviceURI;

	public OltuAuthorizationController() {

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

	/**
	 * Begin authentication procedure by redirecting to remote authorization
	 * location
	 * 
	 * @param serviceURI
	 *            The base URI of the rest api service
	 * @param clientID
	 *            The Client ID to connect with
	 * @param clientSecret
	 *            The client secret to connect with
	 * @param redirect
	 *            Page to redirect to after auth is complete
	 * @return ModelAndView redirecting to the authorization location
	 * @throws OAuthSystemException
	 */
	public ModelAndView authenticate(String serviceURI, String clientID, String clientSecret, String redirect)
			throws OAuthSystemException {
		// save the client credentials and information
		this.clientId = clientID;
		this.clientSecret = clientSecret;
		this.serviceURI = serviceURI;

		// build the authorization path
		URI serviceAuthLocation = UriBuilder.fromUri(serviceURI).path("oauth").path("authorize").build();

		logger.debug("redirect: " + redirect);

		// build a redirect URI to redirect to after auth flow is completed
		String tokenRedirect = buildRedirectURI(redirect);

		// build the redirect query to request an authorization code from the
		// remote API
		OAuthClientRequest request = OAuthClientRequest.authorizationLocation(serviceAuthLocation.toString())
				.setClientId(clientID).setRedirectURI(tokenRedirect).setResponseType(ResponseType.CODE.toString())
				.setScope("read").buildQueryMessage();

		String locURI = request.getLocationUri();
		logger.debug("authorization request location:" + locURI);

		// create the redirection
		ModelAndView modelAndView = new ModelAndView(new RedirectView(locURI));
		return modelAndView;
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
	@RequestMapping("/token")
	public ModelAndView getToken(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("redirect") String redirect) throws IOException, OAuthSystemException, OAuthProblemException,
			URISyntaxException {

		// Get the OAuth2 auth code
		OAuthAuthzResponse oar = OAuthAuthzResponse.oauthCodeAuthzResponse(request);
		String code = oar.getCode();
		logger.debug("got code " + code);

		// Read the RemoteAPI from the RemoteAPIService and get the base URI

		// Build the token location for this service
		URI serviceTokenLocation = UriBuilder.fromUri(serviceURI).path("oauth").path("token").build();
		logger.debug("token loc " + serviceTokenLocation);

		// Build the redirect URI to request a token from
		String tokenRedirect = buildRedirectURI(redirect);

		// Create the token request form the given auth code
		OAuthClientRequest tokenRequest = OAuthClientRequest.tokenLocation(serviceTokenLocation.toString())
				.setClientId(clientId).setClientSecret(clientSecret).setRedirectURI(tokenRedirect).setCode(code)
				.setGrantType(GrantType.AUTHORIZATION_CODE).buildBodyMessage();

		// execute the request
		OAuthClient client = new OAuthClient(new URLConnectionClient());

		// read the response for the access token
		OAuthJSONAccessTokenResponse accessTokenResponse = client.accessToken(tokenRequest,
				OAuthJSONAccessTokenResponse.class);
		String accessToken = accessTokenResponse.getAccessToken();

		// check the token expiry
		Long expiresIn = accessTokenResponse.getExpiresIn();
		logger.debug("Token expires in " + expiresIn);

		// adding the token to the response page. This is just a demo to show
		// how to get an oauth token. NEVER DO THIS!!!
		redirect = redirect + "?token=" + accessToken;

		// redirect the response back to the requested resource
		return new ModelAndView(new RedirectView(redirect));
	}

	/**
	 * Build the redirect URI for the token page with the API and resource page
	 * 
	 * @param redirectPage
	 *            the resource page to redirect to once the authorization is
	 *            complete
	 * @return
	 */
	private String buildRedirectURI(String redirectPage) {

		URI build = UriBuilder.fromUri(tokenRedirect).queryParam("redirect", redirectPage).build();
		return build.toString();
	}
}
