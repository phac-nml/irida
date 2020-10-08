package ca.corefacility.bioinformatics.irida.ria.web.oauth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.oltu.oauth2.client.response.OAuthAuthzResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for handling OAuth2 authorization codes for the Galaxy exporter
 * 
 * 
 *
 */

@Controller
public class GalaxyRedirectionEndpointController {

	private static final Logger logger = LoggerFactory.getLogger(GalaxyRedirectionEndpointController.class);

	/**
	 * Receive the OAuth2 authorization code from IRIDA and pass it on to the client-side code
	 * @param model
	 *            the model to write to
	 * @param request
	 *            the incoming request
	 * @param session
	 *            the user's session
	 * @return a template that will pass on the authorization code
	 * @throws OAuthProblemException if a valid OAuth authorization response cannot be created
	 * @throws IllegalStateException if the callback URL is removed from an invalid session
	 */
	@RequestMapping("galaxy/auth_code")
	public String passAuthCode(Model model, HttpServletRequest request, HttpSession session
			) throws OAuthProblemException, IllegalStateException {
		logger.debug("Parsing auth code from HttpServletRequest");
		// Get the OAuth2 authorization code
		OAuthAuthzResponse oar = OAuthAuthzResponse.oauthCodeAuthzResponse(request);
		String code = oar.getCode();
		model.addAttribute("auth_code", code);
		
		session.removeAttribute("galaxyExportToolCallbackURL");
		
		return "templates/galaxy_auth_code.tmpl";
	}

	/**
	 * Get the URL for the galaxy redirection location.  This will be needed for the oauth flow to get its token.
	 *
	 * @param baseURL The server's base URL
	 * @return the URL of the galaxy oauth redirect location.
	 */
	public static String getGalaxyRedirect(String baseURL) {
		return baseURL + "/galaxy/auth_code";
	}
}