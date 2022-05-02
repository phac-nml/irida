package ca.corefacility.bioinformatics.irida.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ca.corefacility.bioinformatics.irida.IridaApplication;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import ca.corefacility.bioinformatics.irida.config.IridaApiGalaxyTestConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestFilesystemConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;

/**
 * Annotation that is to be specified on Galaxy integration tests. Simplifies
 * the configuration of tests by automatically adding a number of necessary
 * annotations.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited

@Tag("IntegrationTest")
@Tag("Galaxy")
@ActiveProfiles("test")
@SpringBootTest(classes = { IridaApplication.class,
		IridaApiTestFilesystemConfig.class, IridaApiGalaxyTestConfig.class })
public @interface GalaxyIntegrationTest {

}
