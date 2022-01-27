package ca.corefacility.bioinformatics.irida.ria.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * Interceptor to add analytics to every page.
 */
public class AnalyticsHandlerInterceptor implements AsyncHandlerInterceptor {
	private final String analytics;

	public AnalyticsHandlerInterceptor(String analyticsString) {
		this.analytics = analyticsString;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void postHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler,
			final ModelAndView modelAndView) throws Exception {

		// ensure the request isn't for the rest api
		if (!request.getServletPath().startsWith("/api") && modelAndView != null
				&& !modelAndView.getViewName().startsWith("redirect")) {
			modelAndView.getModelMap().addAttribute("analytics", analytics);
		}
	}
}
