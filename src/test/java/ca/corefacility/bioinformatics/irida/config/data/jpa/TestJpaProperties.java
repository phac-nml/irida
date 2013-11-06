
package ca.corefacility.bioinformatics.irida.config.data.jpa;

import ca.corefacility.bioinformatics.irida.config.data.jpa.JpaProperties;
import java.util.Properties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Configuration
@Profile("test")
public class TestJpaProperties implements JpaProperties{

	@Override
	public Properties getJpaProperties() {
		return new Properties();
	}

}
