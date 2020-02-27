package ca.corefacility.bioinformatics.irida.ria.config;

import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

/**
 * Interceptor for handling UI BreadCrumbs
 */
@Component
public class BreadCrumbInterceptor extends HandlerInterceptorAdapter {
	private static final Logger logger = LoggerFactory.getLogger(BreadCrumbInterceptor.class);

	@Autowired
	private ProjectService projectService;

	@Autowired
	private SampleService sampleService;

	@Autowired
	private MessageSource messageSource;

	Set<String> BASE_CRUMBS = ImmutableSet.of("projects", "samples", "export", "settings");

	/**
	 * Constructor
	 */
	public BreadCrumbInterceptor() {
	}

	public BreadCrumbInterceptor(ProjectService projectService, SampleService sampleService,
			MessageSource messageSource) {
		this.projectService = projectService;
		this.sampleService = sampleService;
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

		if (!hasGoodPath(servletPath) || !hasGoodModelAndView(modelAndView)) {
			// No breadcrumbs required here.
			return;
		}

		/*
		Keep track of each breadcrumb in a list, this is the order they will
		be rendered in the UI.
		 */
		List<BreadCrumb> breadCrumbs = new ArrayList<>();
		Locale locale = request.getLocale();

		/*
		Need the context path in case of servlet container path.
		 */
		String contextPath = request.getContextPath();

		/*
		Each href for a breadcrumb is an extension on the current so we will
		just build up one big one.
		 */
		StringBuilder url = new StringBuilder(contextPath);

		/*
		Break the url into parts, each "part" will become a breadcrumb.
		 */
		List<String> partsList = Arrays.stream(servletPath.split("/"))
				.filter(part -> !Strings.isNullOrEmpty(part))
				.collect(Collectors.toList());
		ListIterator<String> parts = partsList.listIterator();

		while (parts.hasNext()) {
			String next = parts.next();

			/*
			If this is this is the last crumb, then don't do anything with it since it is the current page.
			 */
			if (parts.hasNext()) {
				url.append("/");
				if (BASE_CRUMBS.contains(next)) {

					/*
					Check to see if it is a base crumb -> these will need internationalization
					 */
					breadCrumbs.add(new BreadCrumb(messageSource.getMessage("bc." + next, new Object[] {}, locale),
							url.append(next)
									.toString()));
				} else if (NumberUtils.isNumber(next)) {
					String parent = partsList.get(parts.previousIndex() - 1);
					if (parent.equals("projects")) {
						Project project = projectService.read(Long.parseLong(next));
						breadCrumbs.add(new BreadCrumb(project.getLabel(), url.append(next)
								.toString()));
					} else if (parent.equals("samples")) {
						Sample sample = sampleService.read(Long.parseLong(next));
						breadCrumbs.add(new BreadCrumb(sample.getLabel(), url.append(next)
								.toString()));
					}
				}

			}
		}
		modelAndView.getModelMap()
				.put("breadcrumbs", breadCrumbs);

	}

	/**
	 * Try to get the i18n message for a noun, if it doesn't exist, return the noun as is.
	 *
	 * @param noun   Breadcrumb noun to get i18n message for (i.e. "bc.{noun}")
	 * @param locale Locale
	 * @return Internationalized message or original noun if message doesn't exist (e.g. noun is an id number)
	 */
	private String tryGetMessage(String noun, Locale locale) {
		final String message = messageSource.getMessage("bc." + noun, null, locale);
		return Strings.isNullOrEmpty(message) ? noun : message;
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

		// Need to remove the leading '/'
		path = path.substring(1);

		for (String crumb : BASE_CRUMBS) {
			if (path.startsWith(crumb)) {
				return true;
			}
		}
		return false;
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

	private static class BreadCrumb {
		private String label;
		private String url;

		public BreadCrumb(String label, String url) {
			this.label = label;
			this.url = url;
		}

		public String getLabel() {
			return label;
		}

		public String getUrl() {
			return url;
		}
	}
}
