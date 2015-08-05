package ca.corefacility.bioinformatics.irida.ria.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.FilterInvocation;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Interceptor Adaptor to add the user to the {@link Model} each server call.
 */
public class UserSecurityInterceptor extends HandlerInterceptorAdapter {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		if (modelAndView != null) {
			ServletRequest req = (ServletRequest) request;
			ServletResponse resp = (ServletResponse) response;
			FilterInvocation filterInvocation = new FilterInvocation(req, resp, new FilterChain() {
				public void doFilter(ServletRequest request, ServletResponse response) throws IOException,
						ServletException {
					throw new UnsupportedOperationException();
				}
			});

			if (isAuthenticated()) {
				UserDetails currentUserDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				if (currentUserDetails != null) {
					modelAndView.getModel().put("currentUser", currentUserDetails);
				}
			}
		}
	}

	private boolean isAuthenticated() {
		return SecurityContextHolder.getContext().getAuthentication() instanceof UsernamePasswordAuthenticationToken;
	}
}
