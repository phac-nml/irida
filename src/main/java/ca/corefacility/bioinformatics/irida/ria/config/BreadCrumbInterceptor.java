package ca.corefacility.bioinformatics.irida.ria.config;

import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Interceptor for handling UI BreadCrumbs
 */
public class BreadCrumbInterceptor extends HandlerInterceptorAdapter {
	private static final Logger logger = LoggerFactory.getLogger(BreadCrumbInterceptor.class);
	private final MessageSource messageSource;
	private List<String> BASE_CRUMBS = ImmutableList.of("/projects", "/samples", "/export", "/settings");

	/**
	 * Constructor
	 *
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

		if (hasGoodPath(servletPath) && hasGoodModelAndView(modelAndView)) {
			List<String> parts = Arrays.stream(servletPath.split("/"))
					.filter(part -> !Strings.isNullOrEmpty(part))
					.collect(Collectors.toList());

			Locale locale = request.getLocale();
			List<Map<String, String>> crumbs = new ArrayList<>();

			// Check to ensure that there is some sort of context path.
			String contextPath = request.getContextPath();

			StringBuilder url = new StringBuilder(contextPath);

			try {
				for (String noun : parts) {
					url.append("/");
					url.append(noun);
					crumbs.add(ImmutableMap.of("text", tryGetMessage(noun, locale), "url", url.toString()));
				}
				// Add the breadcrumbs to the model
				modelAndView.getModelMap()
						.put("crumbs", crumbs);
			} catch (NoSuchMessageException e) {
				logger.debug("Missing internationalization for breadcrumb", e.getMessage());
			}
		}
	}

	/**
	 * Try to get the i18n message for a noun, if it doesn't exist, return the noun as is.
	 *
	 * @param noun   Breadcrumb noun to get i18n message for (i.e. "bc.{noun}")
	 * @param locale Locale
	 * @return Internationalized message or original noun if message doesn't exist (e.g. noun is an id number)
	 */
	private String tryGetMessage(String noun, Locale locale) {
		try {
			final String message = messageSource.getMessage("bc." + noun, null, locale);
			return Strings.isNullOrEmpty(message) ? noun : message;
		} catch (NoSuchMessageException e) {
			return noun;
		}
	}

	/**
	 * Check to ensure that the servlet path is valid for breadcrumbs
	 *
	 * @param path Servlet Path
	 * @return true if the path is valid to get breadcrumbs added.
	 */
	private boolean hasGoodPath(String path) {
		if (Strings.isNullOrEmpty(path)) {
			return false;
		}

		boolean goodPath = false;
		for (String crumb : BASE_CRUMBS) {
			goodPath = goodPath || path.startsWith(crumb);
		}
		return goodPath;
	}

	/**
	 * Check to ensure that the {@link ModelAndView} exists and is not in a redirect
	 *
	 * @param modelAndView {@link ModelAndView}
	 * @return true if the {@link ModelAndView} is good for breadcrumbs
	 */
	private boolean hasGoodModelAndView(ModelAndView modelAndView) {
		return modelAndView != null && !modelAndView.getViewName()
				.contains("redirect:");
	}
}
