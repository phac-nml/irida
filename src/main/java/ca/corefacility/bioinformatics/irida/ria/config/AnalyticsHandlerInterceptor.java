package ca.corefacility.bioinformatics.irida.ria.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.google.common.base.Strings;

/**
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Interceptor to add analytics to every page.
 */
@Configuration
@PropertySource(value = { "classpath:configuration.properties", "file:/etc/irida/web.conf" }, ignoreResourceNotFound = true)
public class AnalyticsHandlerInterceptor extends HandlerInterceptorAdapter {
	@Value("${analytics.on}")
	Boolean useAnalytics;
	
	@Value("${analytics.google}")
	String googleAnalytics;

	@Value("${analytics.piwik.url}")
	String piwikAnalyticsUrl;
	
	@Value("${analytics.piwik.siteId}")
	String piwikAnalyticsSiteId;

	@Override
	public void postHandle(final HttpServletRequest request,
			final HttpServletResponse response, final Object handler,
			final ModelAndView modelAndView) throws Exception {

		if (modelAndView != null && useAnalytics) {
			if (!Strings.isNullOrEmpty(piwikAnalyticsUrl) && !Strings.isNullOrEmpty(piwikAnalyticsSiteId)) {
				modelAndView.getModelMap().addAttribute("analyticsPiwikUrl", piwikAnalyticsUrl);
				modelAndView.getModelMap().addAttribute("analyticsPiwikSiteId", piwikAnalyticsSiteId);
			}
			if (!Strings.isNullOrEmpty(googleAnalytics)) {
				modelAndView.getModelMap().addAttribute("analyticsGoogle", googleAnalytics);
			}
		}
	}
}
