package ca.corefacility.bioinformatics.irida.ria.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;

import com.timgroup.jgravatar.Gravatar;
import com.timgroup.jgravatar.GravatarDefaultImage;
import com.timgroup.jgravatar.GravatarRating;

/**
 * Handles actions for when a user is successfully logged in.
 */
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	private static final String GRAVATAR_ATTRIBUTE = "gravatar";

	private UserRepository userRepository;

	public LoginSuccessHandler(UserRepository userRepository){
		this.userRepository = userRepository;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Authentication authentication)
			throws IOException, ServletException {
		super.onAuthenticationSuccess(httpServletRequest, httpServletResponse, authentication);

		HttpSession session = httpServletRequest.getSession();
		User user = (User) authentication.getPrincipal();

		// Add gravatar url as to the session for use in thymeleaf templates.
		Gravatar gravatar = new Gravatar(25, GravatarRating.GENERAL_AUDIENCES, GravatarDefaultImage.IDENTICON);
		String gravatarUrl = gravatar.getUrl(user.getEmail());
		session.setAttribute(GRAVATAR_ATTRIBUTE, gravatarUrl);

		userRepository.updateLogin(user, new Date());
	}
}