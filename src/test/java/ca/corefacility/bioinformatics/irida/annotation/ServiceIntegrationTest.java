package ca.corefacility.bioinformatics.irida.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.springtestdbunit.DbUnitTestExecutionListener;

import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.IridaApplication;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestFilesystemConfig;

/**
 * Annotation that is to be specified on Service layer integration tests.
 * Simplifies the configuration of tests by automatically adding a number of
 * necessary annotations.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited

@Tag("IntegrationTest")
@Tag("Service")
@ActiveProfiles("it")
@SpringBootTest(classes = { IridaApplication.class,
		IridaApiTestFilesystemConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExecutionListener.class })
public @interface ServiceIntegrationTest {

}
