package ca.corefacility.bioinformatics.irida.ria.security;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
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

		HttpSession session = httpServletRequest.getSession();
		User user = (User) authentication.getPrincipal();

		//set the user's selected locale
		try {

			/*
			Get the context path, used by the client JS for requests
			 */
			String contextPath = httpServletRequest.getContextPath();
			String cp = contextPath;
			if(!cp.endsWith("/")) {
				// If the context path is set to anything other the "/" it will look like "/foobar"
				// the UI needs the final slash.
				cp = cp + "/";
			}
			Cookie pathCookie = new Cookie("cp", cp);
			pathCookie.setPath(contextPath);
			httpServletResponse.addCookie(pathCookie);
		} catch (NullPointerException ex) {
			logger.warn("Locale cannot be resolved for " + user.getLocale() + ".  Setting system default locale.");
			localeResolver.setLocale(httpServletRequest, httpServletResponse, Locale.getDefault());
		}

		userRepository.updateLogin(user, new Date());
		super.onAuthenticationSuccess(httpServletRequest, httpServletResponse, authentication);
	}
}