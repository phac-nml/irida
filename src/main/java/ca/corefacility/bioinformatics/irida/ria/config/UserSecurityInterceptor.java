package ca.corefacility.bioinformatics.irida.ria.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * Interceptor Adaptor to add the user to the {@link Model} each server call. Also ensures {@link Role#ROLE_SEQUENCER}
 * users cannot do anything in the UI.
 */
public class UserSecurityInterceptor implements AsyncHandlerInterceptor {
	public static final String CURRENT_USER_DETAILS = "CURRENT_USER_DETAILS";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		HttpSession session = request.getSession();

		User userDetails = (User) session.getAttribute(CURRENT_USER_DETAILS);
		if (userDetails == null && isAuthenticated()) {
			UserDetails currentUserDetails = (UserDetails) SecurityContextHolder.getContext()
					.getAuthentication()
					.getPrincipal();

			// Disallow SEQUENCER role from doing anything in the IRIDA UI
			if (currentUserDetails.getAuthorities().contains(Role.ROLE_SEQUENCER)) {
				session.invalidate();
				throw new AccessDeniedException("Sequencer should not be able to interact with IRIDA UI");
			}

			session.setAttribute(CURRENT_USER_DETAILS, currentUserDetails);
		}
	}

	private boolean isAuthenticated() {
		return SecurityContextHolder.getContext().getAuthentication() instanceof UsernamePasswordAuthenticationToken;
	}
}
