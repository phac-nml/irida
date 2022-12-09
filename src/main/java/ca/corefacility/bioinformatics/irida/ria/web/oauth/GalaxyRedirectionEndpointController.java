package ca.corefacility.bioinformatics.irida.ria.web.oauth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.openid.connect.sdk.AuthenticationErrorResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponseParser;

/**
 * Controller for handling OAuth2 authorization codes for the Galaxy exporter
 */

@Controller
public class GalaxyRedirectionEndpointController {

	private static final Logger logger = LoggerFactory.getLogger(GalaxyRedirectionEndpointController.class);

	public static final String GALAXY_OAUTH_REDIRECT = "/galaxy/auth_code";

	/**
	 * Receive the OAuth2 authorization code from IRIDA and pass it on to the client-side code
	 * 
	 * @param model   the model to write to
	 * @param request the incoming request
	 * @param session the user's session
	 * @return a template that will pass on the authorization code
	 * @throws IllegalStateException if the callback URL is removed from an invalid session
	 * @throws ParseException
	 */
	@RequestMapping(GALAXY_OAUTH_REDIRECT)
	public String passAuthCode(Model model, HttpServletRequest request, HttpSession session)
			throws IllegalStateException, ParseException {
		logger.debug("Parsing auth code from HttpServletRequest");

		// Get the OAuth2 authorization code
		AuthenticationResponse authResponse = AuthenticationResponseParser
				.parse(new ServletServerHttpRequest(request).getURI());

		if (authResponse instanceof AuthenticationErrorResponse) {
			logger.trace("Unexpected authentication response");
		}

		String code = authResponse.toSuccessResponse().getAuthorizationCode().getValue();
		model.addAttribute("auth_code", code);

		session.removeAttribute("galaxyExportToolCallbackURL");

		return "templates/galaxy_auth_code.tmpl";
	}

	/**
	 * Get the URL for the galaxy redirection location. This will be needed for the oauth flow to get its token.
	 *
	 * @param baseURL The server's base URL
	 * @return the URL of the galaxy oauth redirect location.
	 */
	public static String getGalaxyRedirect(String baseURL) {
		return baseURL + GALAXY_OAUTH_REDIRECT;
	}
}