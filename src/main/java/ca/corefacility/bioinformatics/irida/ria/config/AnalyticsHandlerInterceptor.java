package ca.corefacility.bioinformatics.irida.ria.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Interceptor to add analytics to every page.
 */
@Configuration
@PropertySource(value = { "classpath:configuration.properties", "file:/etc/irida/web.conf" }, ignoreResourceNotFound = true)
public class AnalyticsHandlerInterceptor extends HandlerInterceptorAdapter {
	private static final Logger logger = LoggerFactory.getLogger(AnalyticsHandlerInterceptor.class);
	
	@Value("#{'${analytics}'.split(',')}")
	List<String> analytics;

	@Override
	public void postHandle(final HttpServletRequest request,
			final HttpServletResponse response, final Object handler,
			final ModelAndView modelAndView) throws Exception {

		if (modelAndView != null) {
			List<String> analyiticsList = new ArrayList<>();
			for (String analytic : analytics) {
				String pathString = "/etc/irida/analytics/" + analytic + ".html";
				Path path = Paths.get(pathString);
				if (Files.exists(path)) {
					List<String> lines = Files.readAllLines(path);
					StringBuilder builder = new StringBuilder();
					for (String line : lines) {
						builder.append(line);
						builder.append("\n");
					}
					analyiticsList.add(builder.toString());
				} else {
					logger.debug("Cannot find analytic file: ", pathString);
				}
			}
			modelAndView.getModelMap().addAttribute("analytics", analyiticsList);
		}
	}
}
