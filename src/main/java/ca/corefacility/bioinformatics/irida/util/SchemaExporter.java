package ca.corefacility.bioinformatics.irida.util;

import java.io.IOException;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;

/**
 * Export the current hibernate-generated schema (with auditing tables).
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
public class SchemaExporter {
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
			context.getEnvironment().setActiveProfiles("dev");
			context.register(IridaApiServicesConfig.class);
			context.refresh();
		}
	}
}
