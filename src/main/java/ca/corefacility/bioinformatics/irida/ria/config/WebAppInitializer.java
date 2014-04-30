package ca.corefacility.bioinformatics.irida.ria.config;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

/**
 * This class allows us to configure the ServletContext pragmatically.
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 * 
 */
public class WebAppInitializer implements WebApplicationInitializer {
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		WebApplicationContext context = getContext();
		servletContext.addListener(new ContextLoaderListener(context));
		ServletRegistration.Dynamic dispatcher = servletContext.addServlet("DispatcherServlet", new DispatcherServlet(
				context));

		// Give this servlet a high priority for startup
		dispatcher.setLoadOnStartup(Constants.STARTUP_LOAD_PRIORITY_HIGH);

		// Add a servlet mapping
		dispatcher.addMapping(Constants.DEFAULT_MAPPING_URL);
	}

	/**
	 * Get a new {@link AnnotationConfigWebApplicationContext} and tell it where
	 * to look for configuration files.
	 * 
	 * @return {@link AnnotationConfigWebApplicationContext}
	 */
	private AnnotationConfigWebApplicationContext getContext() {
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();

		// Give a hint to the context where classes marked with @Configuration
		// are located.
		context.setConfigLocation(Constants.CONFIG_LOCATION);
		return context;
	}
}
