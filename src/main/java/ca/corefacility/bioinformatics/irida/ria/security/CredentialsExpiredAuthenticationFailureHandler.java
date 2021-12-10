package ca.corefacility.bioinformatics.irida.ria.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.PasswordResetController;
import ca.corefacility.bioinformatics.irida.service.user.PasswordResetService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.ImmutableList;

/**
 * AuthenticationFailureHandler used to handle a CredentialsExpiredException.
 * This handler will create a new {@link PasswordReset} and redirect to the
 * appropriate {@link PasswordResetController} page.
 * 
 *
 */
@Component
public class CredentialsExpiredAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
	private static final Logger logger = LoggerFactory.getLogger(CredentialsExpiredAuthenticationFailureHandler.class);

	private final PasswordResetService resetService;
	private final UserService userService;

	@Autowired
	public CredentialsExpiredAuthenticationFailureHandler(PasswordResetService resetService, UserService userService) {
		this.resetService = resetService;
		this.userService = userService;
	}

	/**
	 * Handle CredentialsExpiredException and create a {@link PasswordReset}. If
	 * not CredentialsExpiredException pass to super.
	 */
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		if (exception instanceof CredentialsExpiredException) {
			// get the username of the user who tried to login
			String username = request.getParameter("username");
			logger.trace("Password is expired for [ " + username + " ].  Generating a password reset.");

			// set an anonymous auth token
			setAuthentication();

			// get the user and create a password reset
			User userByUsername = userService.getUserByUsername(username);
			PasswordReset create = resetService.create(new PasswordReset(userByUsername));

			// Clear the anonymous auth token
			SecurityContextHolder.clearContext();

			// redirect the user to the password reset page
			String contextPath = request.getContextPath();
			String resetId = create.getId();
			response.sendRedirect(contextPath + "/password_reset/" + resetId + "?expired=true");

		} else {
			super.onAuthenticationFailure(request, response, exception);
		}

	}

	/**
	 * Set an anonymous authentication token
	 */
	private void setAuthentication() {
		AnonymousAuthenticationToken anonymousToken = new AnonymousAuthenticationToken("nobody", "nobody",
				ImmutableList.of(Role.ROLE_ANONYMOUS));
		SecurityContextHolder.getContext().setAuthentication(anonymousToken);
	}
}
