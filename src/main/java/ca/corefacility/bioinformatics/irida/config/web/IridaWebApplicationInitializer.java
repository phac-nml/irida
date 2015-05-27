package ca.corefacility.bioinformatics.irida.config.web;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import ca.corefacility.bioinformatics.irida.config.security.IridaWebSecurityConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.web.filter.HttpHeadFilter;

import com.github.dandelion.core.web.DandelionFilter;
import com.github.dandelion.core.web.DandelionServlet;

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

		// Register the Dandelion filter
		FilterRegistration.Dynamic dandelionFilter = servletContext.addFilter("dandelionFilter", new DandelionFilter());
		dandelionFilter.addMappingForUrlPatterns(null, false, "/*");

		// Register the Dandelion servlet
		ServletRegistration.Dynamic dandelionServlet = servletContext.addServlet("dandelionServlet",
				new DandelionServlet());
		dandelionServlet.setLoadOnStartup(2);
		dandelionServlet.addMapping("/dandelion-assets/*");
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}

	@Override
	protected Filter[] getServletFilters() {
		return new Filter[] { new HttpHeadFilter() };
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
