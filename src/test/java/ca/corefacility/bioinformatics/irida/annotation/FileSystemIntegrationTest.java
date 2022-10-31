package ca.corefacility.bioinformatics.irida.annotation;

import java.lang.annotation.*;

import org.junit.jupiter.api.Tag;

/**
 * Annotation that is to be specified on file system integration tests.
 * Simplifies the configuration of tests by automatically adding a number of
 * necessary annotations.
 */

@Tag("IntegrationTest")
@Tag("FileSystem")
public @interface FileSystemIntegrationTest {
}
