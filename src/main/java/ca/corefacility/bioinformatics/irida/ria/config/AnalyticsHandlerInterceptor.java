package ca.corefacility.bioinformatics.irida.ria.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Interceptor to add analytics to every page.
 */
public class AnalyticsHandlerInterceptor extends HandlerInterceptorAdapter {
	private static final Logger logger = LoggerFactory.getLogger(AnalyticsHandlerInterceptor.class);
	private final String analytics;

	public AnalyticsHandlerInterceptor(String analyticsString) {
		this.analytics = analyticsString;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void postHandle(final HttpServletRequest request,
			final HttpServletResponse response, final Object handler,
			final ModelAndView modelAndView) throws Exception {

		if (modelAndView != null) {
			modelAndView.getModelMap().addAttribute("analytics", analytics);
		}
	}
}
