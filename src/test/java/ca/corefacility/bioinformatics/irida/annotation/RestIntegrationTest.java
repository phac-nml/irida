package ca.corefacility.bioinformatics.irida.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import ca.corefacility.bioinformatics.irida.config.IridaIntegrationTestUriConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestFilesystemConfig;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited

@Tag("IntegrationTest")
@Tag("Rest")
@ActiveProfiles("it")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import({ IridaIntegrationTestUriConfig.class, IridaApiTestFilesystemConfig.class })
public @interface RestIntegrationTest {
}
