package ca.corefacility.bioinformatics.irida.annotation;

import java.lang.annotation.*;

import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.IridaApplication;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestFilesystemConfig;

import com.github.springtestdbunit.DbUnitTestExecutionListener;

/**
 * Annotation that is to be specified on Service layer integration tests. Simplifies the configuration of tests by
 * automatically adding a number of necessary annotations.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited

@Tag("IntegrationTest")
@Tag("Service")
@ActiveProfiles("it")
@SpringBootTest(classes = {
		IridaApplication.class, IridaApiTestFilesystemConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT,
		properties = { "delete.from.filesystem=true" })
@TestExecutionListeners({
		DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExecutionListener.class })
public @interface ServiceIntegrationTest {

}
