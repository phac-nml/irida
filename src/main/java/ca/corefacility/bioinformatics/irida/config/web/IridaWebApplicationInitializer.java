package ca.corefacility.bioinformatics.irida.config.web;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.support.AbstractDispatcherServletInitializer;

import ca.corefacility.bioinformatics.irida.web.filter.HttpHeadFilter;

/**
 * REST API initializer with security.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
public class IridaWebApplicationInitializer extends AbstractDispatcherServletInitializer {

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
	public WebApplicationContext createServletApplicationContext() {
		final AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.register(IridaRestApiWebConfig.class);
		return context;
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}

	@Override
	protected WebApplicationContext createRootApplicationContext() {
		return null;
	}

	@Override
	protected Filter[] getServletFilters() {
		return new Filter[] { new HttpHeadFilter() };
	}
}
