package ca.corefacility.bioinformatics.irida.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import ca.corefacility.bioinformatics.irida.util.RecursiveDeleteVisitor;

@Configuration
public class IridaApiFilesystemRepositoryConfig {

	private static final Logger logger = LoggerFactory.getLogger(IridaApiFilesystemRepositoryConfig.class);

	private @Value("${sequence.file.base.directory}") String sequenceFileBaseDirectory;

	private @Value("${reference.file.base.directory}") String referenceFileBaseDirectory;

	private static final Set<Path> BASE_DIRECTORIES = new HashSet<>();

	@Autowired
	private Environment environment;

	// Franklin: I assume that the scope of a configuration bean is the lifetime
	// of the application, so the directory should only get deleted *after* the
	// tests have finished running.
	@PreDestroy
	public void tearDown() throws IOException {
		for (Path b : BASE_DIRECTORIES) {
			Files.walkFileTree(b, new RecursiveDeleteVisitor());
		}
	}

	@Profile("prod")
	@Bean(name = "referenceFileBaseDirectory")
	public Path referenceFileBaseDirectoryProd() {
		return getExistingPathOrThrow(referenceFileBaseDirectory);
	}

	@Profile("prod")
	@Bean(name = "sequenceFileBaseDirectory")
	public Path sequenceFileBaseDirectoryProd() {
		return getExistingPathOrThrow(sequenceFileBaseDirectory);
	}

	@Profile("dev")
	@Bean(name = "referenceFileBaseDirectory")
	public Path referenceFileBaseDirectory() throws IOException {
		return configureDirectory(referenceFileBaseDirectory, "reference-file-dev");
	}

	@Profile("dev")
	@Bean(name = "sequenceFileBaseDirectory")
	public Path sequenceFileBaseDirectory() throws IOException {
		return configureDirectory(sequenceFileBaseDirectory, "sequence-file-dev");
	}

	private Path getExistingPathOrThrow(String directory) {
		Path baseDirectory = Paths.get(directory);
		if (!Files.exists(baseDirectory)) {
			throw new IllegalStateException(String.format(
					"Cannot continue startup; base directory [%s] does not exist!", baseDirectory));
		} else {
			logger.info(
					String.format("Using specified existing directory at [%s]. The directory *will not* be removed at shutdown time."),
					baseDirectory);
		}
		return baseDirectory;
	}

	private Path configureDirectory(String pathName, String defaultDevPathPrefix) throws IOException {
		Path baseDirectory = Paths.get(pathName);
		if (!Files.exists(baseDirectory)) {
			baseDirectory = Files.createTempDirectory(defaultDevPathPrefix);
			BASE_DIRECTORIES.add(baseDirectory);
			logger.info(String
					.format("The directory [%s] does not exist, but it looks like you're running in a dev environment, "
							+ "so I created a temporary location at [%s]. This directory *will* be removed at shutdown time.",
							pathName, baseDirectory.toString()));
		}
		return baseDirectory;
	}
}
