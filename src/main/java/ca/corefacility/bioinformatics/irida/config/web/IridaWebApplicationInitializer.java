package ca.corefacility.bioinformatics.irida.config.web;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import ca.corefacility.bioinformatics.irida.config.security.IridaWebSecurityConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.web.filter.HttpHeadFilter;
import ca.corefacility.bioinformatics.irida.web.filter.SlashFilter;

/**
 * REST API initializer with security.
 * 
 *
 */
public class IridaWebApplicationInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	public void onStartup(final ServletContext servletContext) throws ServletException {
		// make sure that we load up the database in hibernate by default. This
		// behaviour can be overridden by external configuration files.
		servletContext.setInitParameter("spring.profiles.default", "dev");

		// do the default setup
		super.onStartup(servletContext);

		// install the spring security filter chain.
		final DelegatingFilterProxy springSecurityFilterChain = new DelegatingFilterProxy("springSecurityFilterChain");
		springSecurityFilterChain
				.setContextAttribute("org.springframework.web.servlet.FrameworkServlet.CONTEXT.dispatcher");
		servletContext.addFilter("springSecurityFilterChain", springSecurityFilterChain).addMappingForUrlPatterns(null,
				false, "/*");
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}

	@Override
	protected Filter[] getServletFilters() {
		return new Filter[] { new HttpHeadFilter(), new SlashFilter() };
	}

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class[] { IridaApiServicesConfig.class };
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class[] { IridaRestApiWebConfig.class, IridaUIWebConfig.class, IridaWebSecurityConfig.class };
	}
}
