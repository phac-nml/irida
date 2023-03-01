package ca.corefacility.bioinformatics.irida.annotation;

import java.lang.annotation.*;

import org.junit.jupiter.api.Tag;

/**
 * Annotation that is to be specified on File System integration tests.
 * Simplifies the configuration of tests by automatically adding a number of
 * necessary annotations.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited

@Tag("IntegrationTest")
@Tag("FileSystem")
public @interface FileSystemIntegrationTest {
}
