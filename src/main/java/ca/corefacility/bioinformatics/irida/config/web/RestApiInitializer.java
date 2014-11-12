package ca.corefacility.bioinformatics.irida.config.web;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import ca.corefacility.bioinformatics.irida.web.filter.HttpHeadFilter;

/**
 * Web application initialization class.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
public class RestApiInitializer implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		registerSpringConfiguration(servletContext);
		registerSpringDispatcherServlet(servletContext);
		registerSpringSecurityFilterChain(servletContext);
		registerHeadRequestFilter(servletContext);

		servletContext.setInitParameter("spring.profiles.default", "dev");
	}

	private void registerSpringConfiguration(ServletContext servletContext) {
		AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
		rootContext.register(IridaRestApiWebConfig.class);
		servletContext.addListener(new ContextLoaderListener(rootContext));
	}

	public void registerSpringDispatcherServlet(ServletContext servletContext) {
		AnnotationConfigWebApplicationContext dispatcherContext = new AnnotationConfigWebApplicationContext();
		dispatcherContext.register(DispatcherServlet.class);
		ServletRegistration.Dynamic dispatcher = servletContext.addServlet("irida-rest", new DispatcherServlet(
				dispatcherContext));
		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping("/*");
	}

	public void registerSpringSecurityFilterChain(ServletContext servletContext) {
		FilterRegistration.Dynamic securityFilterChain = servletContext.addFilter("springSecurityFilterChain",
				DelegatingFilterProxy.class);
		securityFilterChain.setInitParameter("contextAttribute",
				"org.springframework.web.servlet.FrameworkServlet.CONTEXT.irida-rest");
		securityFilterChain.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
	}

	public void registerHeadRequestFilter(ServletContext servletContext) {
		FilterRegistration.Dynamic headRequestFilter = servletContext.addFilter("headRequestFilter",
				HttpHeadFilter.class);
		headRequestFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
	}
}