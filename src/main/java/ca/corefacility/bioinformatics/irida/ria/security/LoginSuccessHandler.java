package ca.corefacility.bioinformatics.irida.ria.security;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.web.servlet.LocaleResolver;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;

/**
 * Handles actions for when a user is successfully logged in.
 */
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	private static final Logger logger = LoggerFactory.getLogger(LoginSuccessHandler.class);

	private final UserRepository userRepository;

	private final LocaleResolver localeResolver;

	public LoginSuccessHandler(UserRepository userRepository, LocaleResolver localeResolver) {
		this.userRepository = userRepository;
		this.localeResolver = localeResolver;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Authentication authentication) throws IOException, ServletException {
		super.onAuthenticationSuccess(httpServletRequest, httpServletResponse, authentication);

		HttpSession session = httpServletRequest.getSession();
		User user = (User) authentication.getPrincipal();

		//set the user's selected locale
		try {
			Locale locale = Locale.forLanguageTag(user.getLocale());
			localeResolver.setLocale(httpServletRequest, httpServletResponse, locale);
		} catch (NullPointerException ex) {
			logger.warn("Locale cannot be resolved for " + user.getLocale() + ".  Setting system default locale.");
			localeResolver.setLocale(httpServletRequest, httpServletResponse, Locale.getDefault());
		}

		// Add the user into the session
		session.setAttribute("user", user);

		userRepository.updateLogin(user, new Date());
	}
}