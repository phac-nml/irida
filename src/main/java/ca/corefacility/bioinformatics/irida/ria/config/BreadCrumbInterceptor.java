package ca.corefacility.bioinformatics.irida.ria.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

/**
 * Interceptor for handling UI BreadCrumbs
 */
public class BreadCrumbInterceptor extends HandlerInterceptorAdapter {
	private static final Logger logger = LoggerFactory.getLogger(BreadCrumbInterceptor.class);
	private final MessageSource messageSource;
	private Map<String, Boolean> BASE = ImmutableMap.of(
			"projects", true,
			"samples", true
	);

	/**
	 * Constructor
	 * @param messageSource {@link MessageSource} for internationalization of breadcrumb
	 */
	public BreadCrumbInterceptor(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		super.postHandle(request, response, handler, modelAndView);

		String servletPath = request.getServletPath();
		String[] parts = servletPath.split("/");
		int counter = Strings.isNullOrEmpty(parts[0]) ? 1 : 0;

		if (modelAndView != null && !modelAndView.getViewName().contains("redirect:") && !servletPath.contains("template") && BASE.containsKey(parts[counter])) {
			Locale locale = request.getLocale();
			List<Map<String, String>> crumbs = new ArrayList<>();

			// Check to ensure that there is some sort of context path.
			String contextPath = request.getContextPath();

			StringBuilder url = new StringBuilder(contextPath);

			try {
				for (; counter < parts.length; counter++) {
					// Should be a noun
					String noun = parts[counter];
					url.append("/");
					url.append(noun);

					crumbs.add(
							ImmutableMap.of(
									"text", messageSource.getMessage("bc." + noun, null, locale),
									"url", url.toString())
					);

					// Check to see if there is a next part, if there is it is expected to be an id.
					if (parts.length > ++counter) {
						String id = parts[counter];
						url.append("/");
						url.append(id);
						crumbs.add(
								ImmutableMap.of(
										"text", id,
										"url", url.toString()
								)
						);
					}
				}

				// Add the breadcrumbs to the model
				modelAndView.getModelMap().put("crumbs", crumbs);
			} catch (NoSuchMessageException e) {
				logger.debug("Missing internationalization for breadcrumb", e.getMessage());
			}
		}
	}
}
