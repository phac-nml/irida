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
	
	@Value("${analytics.google.id}")
	String googleAnalyticsId;

	@Value("${analytics.piwik.url}")
	String piwikAnalyticsUrl;
	
	@Value("${analytics.piwik.id}")
	String piwikAnalyticsId;

	@Override
	public void postHandle(final HttpServletRequest request,
			final HttpServletResponse response, final Object handler,
			final ModelAndView modelAndView) throws Exception {

		if (modelAndView != null) {
			if (!Strings.isNullOrEmpty(piwikAnalyticsUrl) && !Strings.isNullOrEmpty(piwikAnalyticsId)) {
				modelAndView.getModelMap().addAttribute("analyticsPiwikUrl", piwikAnalyticsUrl);
				modelAndView.getModelMap().addAttribute("analyticsPiwikId", piwikAnalyticsId);
			}
			if (!Strings.isNullOrEmpty(googleAnalyticsId)) {
				modelAndView.getModelMap().addAttribute("analyticsGoogleId", googleAnalyticsId);
			}
		}
	}
}
