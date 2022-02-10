package ca.corefacility.bioinformatics.irida.ria.config;

import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

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
public class BreadCrumbInterceptor implements AsyncHandlerInterceptor {

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

		String servletPath = request.getServletPath();

		if (!hasGoodPath(servletPath) || !hasGoodModelAndView(modelAndView)) {
			// No breadcrumbs required here.
			return;
		}

		/*
		 * Keep track of each breadcrumb in a list, this is the order they will
		 * be rendered in the UI.
		 */
		List<BreadCrumb> breadCrumbs = new ArrayList<>();
		Locale locale = request.getLocale();

		/*
		 * Need the context path in case of servlet container path.
		 */
		String contextPath = request.getContextPath();

		/*
		 * Each href for a breadcrumb is an extension on the current so we will
		 * just build up one big one.
		 */
		StringBuilder url = new StringBuilder(contextPath);

		/*
		 * Break the url into parts, each "part" will become a breadcrumb.
		 */
		List<String> partsList = Arrays.stream(servletPath.split("/")).filter(part -> !Strings.isNullOrEmpty(part))
				.collect(Collectors.toList());
		ListIterator<String> parts = partsList.listIterator();

		while (parts.hasNext()) {
			String next = parts.next();

			/*
			 * If this is this is the last crumb, then don't do anything with it
			 * since it is the current page.
			 */
			if (parts.hasNext()) {
				url.append("/");
				if (BASE_CRUMBS.contains(next)) {

					/*
					 * Check to see if it is a base crumb -> these will need
					 * internationalization
					 */
					breadCrumbs.add(new BreadCrumb(messageSource.getMessage("bc." + next, new Object[] {}, locale),
							url.append(next).toString()));
				} else if (NumberUtils.isCreatable(next)) {
					String parent = partsList.get(parts.previousIndex() - 1);
					if (parent.equals("projects")) {
						Project project = projectService.read(Long.parseLong(next));
						breadCrumbs.add(new BreadCrumb(project.getLabel(), url.append(next).toString()));
					} else if (parent.equals("samples")) {
						Sample sample = sampleService.read(Long.parseLong(next));
						breadCrumbs.add(new BreadCrumb(sample.getLabel(), url.append(next).toString()));
					}
				}

			}
		}
		modelAndView.getModelMap().put("breadcrumbs", breadCrumbs);

	}

	/**
	 * Check to ensure that the servlet path is valid for breadcrumbs
	 *
	 * @param path
	 *            Servlet Path
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
	 * Check to ensure that the {@link ModelAndView} exists and is not in a
	 * redirect
	 *
	 * @param modelAndView
	 *            {@link ModelAndView}
	 * @return true if the {@link ModelAndView} is good for breadcrumbs
	 */
	private boolean hasGoodModelAndView(ModelAndView modelAndView) {
		return modelAndView != null && !modelAndView.getViewName().contains("redirect:");
	}

	static class BreadCrumb {
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
