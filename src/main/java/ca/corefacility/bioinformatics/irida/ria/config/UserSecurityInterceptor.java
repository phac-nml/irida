package ca.corefacility.bioinformatics.irida.ria.config;

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
		if (hasGoodModelAndView(modelAndView)) {
			ServletRequest req = (ServletRequest) request;
			ServletResponse resp = (ServletResponse) response;
			FilterInvocation filterInvocation = new FilterInvocation(req, resp, (request1, response1) -> {
				throw new UnsupportedOperationException();
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

	/**
	 * Check to ensure that the {@link ModelAndView} exists and is not in a redirect
	 *
	 * @param modelAndView
	 * 		{@link ModelAndView}
	 *
	 * @return true if the {@link ModelAndView} is good for breadcrumbs
	 */
	private boolean hasGoodModelAndView(ModelAndView modelAndView) {
		return modelAndView != null && !modelAndView.getViewName().contains("redirect:");
	}
}
