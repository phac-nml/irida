package ca.corefacility.bioinformatics.irida.example.oauthClient.config;

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

/**
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public class InitializeConfig implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		registerSpringConfiguration(servletContext);
		registerSpringDispatcherServlet(servletContext);
		registerSpringSecurityFilterChain(servletContext);

		servletContext.setInitParameter("spring.profiles.default", "dev");
	}

	private void registerSpringConfiguration(ServletContext servletContext) {
		AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
		rootContext.register(MvcConfiguration.class);
		servletContext.addListener(new ContextLoaderListener(rootContext));
	}

	public void registerSpringDispatcherServlet(ServletContext servletContext) {
		AnnotationConfigWebApplicationContext dispatcherContext = new AnnotationConfigWebApplicationContext();
		dispatcherContext.register(DispatcherServlet.class);
		ServletRegistration.Dynamic dispatcher = servletContext.addServlet("oauthClient", new DispatcherServlet(
				dispatcherContext));
		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping("/");
	}

	public void registerSpringSecurityFilterChain(ServletContext servletContext) {
		FilterRegistration.Dynamic securityFilterChain = servletContext.addFilter("springSecurityFilterChain",
				DelegatingFilterProxy.class);
		securityFilterChain.setInitParameter("contextAttribute",
				"org.springframework.web.servlet.FrameworkServlet.CONTEXT.oauthClient");
		securityFilterChain.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
	}

}
