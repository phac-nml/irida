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

import ca.corefacility.bioinformatics.irida.exceptions.IridaLdapAuthenticationException;
import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.security.SequencerUILoginException;
import ca.corefacility.bioinformatics.irida.service.user.PasswordResetService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.ImmutableList;

/**
 * AuthenticationFailureHandler used to handle specific AuthenticationException's. This handler will handle the
 * exception or defer to default handling from SimpleUrlAuthenticationFailureHandler.
 */
@Component
public class IridaPostAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
	private static final Logger logger = LoggerFactory.getLogger(IridaPostAuthenticationFailureHandler.class);

	private final PasswordResetService resetService;
	private final UserService userService;

	@Autowired
	public IridaPostAuthenticationFailureHandler(PasswordResetService resetService, UserService userService) {
		this.resetService = resetService;
		this.userService = userService;
	}

	/**
	 * Custom authentication failure handling for specific AuthenticationException's otherwise default to
	 * {@link SimpleUrlAuthenticationFailureHandler} behaviour.
	 * Handle CredentialsExpiredException and create a {@link PasswordReset}.
	 * Handle IridaLdapAuthenticationException and redirect with appropriate error code
	 * If not CredentialsExpiredException or IridaLdapAuthenticationException pass to super.
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
		} else if (exception instanceof SequencerUILoginException) {
			// Clear the anonymous auth token
			SecurityContextHolder.clearContext();

			// redirect the user to the login page with the sequencer-login url param
			String contextPath = request.getContextPath();
			response.sendRedirect(contextPath + "/login?error=true&sequencer-login=true");
		} else if (exception instanceof IridaLdapAuthenticationException) {
			logger.trace(exception.toString());
			String contextPath = request.getContextPath();
			response.sendRedirect(contextPath + "/login?ldap-error="+((IridaLdapAuthenticationException) exception).getErrorCode());
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
