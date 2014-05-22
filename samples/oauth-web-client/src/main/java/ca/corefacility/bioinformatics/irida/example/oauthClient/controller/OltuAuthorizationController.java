package ca.corefacility.bioinformatics.irida.example.oauthClient.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.RemoteAPIToken;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;

@Controller
@Scope("session")
public class OltuAuthorizationController {
	private static final Logger logger = LoggerFactory.getLogger(OltuAuthorizationController.class);
	
	private static long ONE_SECOND_IN_MS = 1000;

	private final String tokenRedirect = "http://localhost:8181/token";

	private RemoteAPITokenService tokenService;
	private RemoteAPIService apiService;

	@Autowired
	public OltuAuthorizationController(RemoteAPITokenService tokenService, RemoteAPIService apiRepo) {
		this.tokenService = tokenService;
		this.apiService = apiRepo;
	}

	public ModelAndView authenticate(RemoteAPI remoteAPI, String redirect) throws OAuthSystemException {
		String serviceURI = remoteAPI.getServiceURI();
		URI serviceAuthLocation = UriBuilder.fromUri(serviceURI).path("oauth").path("authorize").build();

		logger.debug("Service: " + remoteAPI);
		logger.debug("redirect: " + redirect);

		String tokenRedirect = buildRedirectURI(remoteAPI.getId(), redirect);

		OAuthClientRequest request = OAuthClientRequest.authorizationLocation(serviceAuthLocation.toString())
				.setClientId(remoteAPI.getClientId()).setRedirectURI(tokenRedirect)
				.setResponseType(ResponseType.CODE.toString()).setScope("read").buildQueryMessage();

		String locURI = request.getLocationUri();
		logger.debug("authorization request location:" + locURI);
		ModelAndView modelAndView = new ModelAndView(new RedirectView(locURI));

		return modelAndView;
	}

	@RequestMapping("/token")
	public ModelAndView getToken(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("apiId") Long apiId, @RequestParam("redirect") String redirect) throws IOException,
			OAuthSystemException, OAuthProblemException, URISyntaxException {

		Long currentTime = System.currentTimeMillis(); // get the current time
														// for the expiry
														// calculation

		OAuthAuthzResponse oar = null;
		String code = null;
		try {
			oar = OAuthAuthzResponse.oauthCodeAuthzResponse(request);
			code = oar.getCode();
		} catch (OAuthProblemException ex) {
			logger.error("OAuth exception: " + ex.getMessage());
			response.sendRedirect("/");
		} catch (NullPointerException ex) {
			logger.error("No code was given");
			response.sendRedirect("/");
		}

		logger.debug("got code " + code);
		RemoteAPI remoteAPI = apiService.read(apiId);
		String serviceURI = remoteAPI.getServiceURI();

		URI serviceTokenLocation = UriBuilder.fromUri(serviceURI).path("oauth").path("token").build();

		logger.debug("token loc " + serviceTokenLocation);

		String tokenRedirect = buildRedirectURI(apiId, redirect);

		OAuthClientRequest tokenRequest = OAuthClientRequest.tokenLocation(serviceTokenLocation.toString())
				.setClientId(remoteAPI.getClientId()).setClientSecret(remoteAPI.getClientSecret())
				.setRedirectURI(tokenRedirect).setCode(code).setGrantType(GrantType.AUTHORIZATION_CODE)
				.buildBodyMessage();

		OAuthClient client = new OAuthClient(new URLConnectionClient());

		OAuthJSONAccessTokenResponse accessTokenResponse = client.accessToken(tokenRequest,
				OAuthJSONAccessTokenResponse.class);
		String accessToken = accessTokenResponse.getAccessToken();
		String refreshToken = accessTokenResponse.getRefreshToken();
		logger.debug("refresh token is: " + refreshToken);

		Long expiresIn = accessTokenResponse.getExpiresIn();
		logger.debug("Token expires in " + expiresIn);

		Date expiry = new Date(currentTime + (expiresIn * ONE_SECOND_IN_MS));
		RemoteAPIToken token = new RemoteAPIToken(accessToken, remoteAPI, expiry);
		tokenService.create(token);

		return new ModelAndView(new RedirectView(redirect));
	}

	private String buildRedirectURI(Long apiId, String redirectPage) {

		URI build = UriBuilder.fromUri(tokenRedirect).queryParam("apiId", apiId).queryParam("redirect", redirectPage)
				.build();
		return build.toString();
	}
}
