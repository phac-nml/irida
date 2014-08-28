package ca.corefacility.bioinformatics.irida.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Config for only activating scheduled tasks in certain profiles.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@Profile({ "dev", "prod", "it" })
@Configuration
@EnableScheduling
public class IridaScheduledTasksConfig {
	
}
