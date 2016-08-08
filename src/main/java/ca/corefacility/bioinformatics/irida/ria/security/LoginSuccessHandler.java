package ca.corefacility.bioinformatics.irida.ria.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import ca.corefacility.bioinformatics.irida.model.user.User;

import com.timgroup.jgravatar.Gravatar;
import com.timgroup.jgravatar.GravatarDefaultImage;
import com.timgroup.jgravatar.GravatarRating;

/**
 * Handles actions for when a user is successfully logged in.
 */
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	private static final Logger logger = LoggerFactory.getLogger(LoginSuccessHandler.class);
	private static final String GRAVATAR_ATTRIBUTE = "gravatar";

	@Override
	public void onAuthenticationSuccess(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Authentication authentication)
			throws IOException, ServletException {
		super.onAuthenticationSuccess(httpServletRequest, httpServletResponse, authentication);

		HttpSession session = httpServletRequest.getSession();
		User user = (User) authentication.getPrincipal();

		// Add gravatar url as to the session for use in thymeleaf templates.
		Gravatar gravatar = new Gravatar(30, GravatarRating.GENERAL_AUDIENCES, GravatarDefaultImage.MONSTERID);
		String gravatarUrl = gravatar.getUrl(user.getEmail());
		logger.info("Adding users gravatar url to the session");
		session.setAttribute(GRAVATAR_ATTRIBUTE, gravatarUrl);

	}
}