package ca.corefacility.bioinformatics.irida.config.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.FilesystemSupplementedRepositoryImpl.RelativePathTranslatorListener;

/**
 * Configuration for filesystem repositories in IRIDA
 */
@Configuration
public class IridaApiFilesystemRepositoryConfig {

	private static final Logger logger = LoggerFactory.getLogger(IridaApiFilesystemRepositoryConfig.class);

	private @Value("${sequence.file.base.directory}")
	String sequenceFileBaseDirectory;

	private @Value("${reference.file.base.directory}")
	String referenceFileBaseDirectory;

	private @Value("${output.file.base.directory}")
	String outputFileBaseDirectory;

	@Autowired
	private ApplicationContext applicationContext;

	@Bean
	public RelativePathTranslatorListener relativePathTranslatorListener(
			final @Qualifier("referenceFileBaseDirectory") Path referenceFileBaseDirectory,
			final @Qualifier("sequenceFileBaseDirectory") Path sequenceFileBaseDirectory,
			final @Qualifier("outputFileBaseDirectory") Path outputFileBaseDirectory) {
		RelativePathTranslatorListener.addBaseDirectory(SequenceFile.class, sequenceFileBaseDirectory);
		RelativePathTranslatorListener.addBaseDirectory(ReferenceFile.class, referenceFileBaseDirectory);
		RelativePathTranslatorListener.addBaseDirectory(AnalysisOutputFile.class, outputFileBaseDirectory);
		return new RelativePathTranslatorListener();
	}

	@Bean(name = "referenceFileBaseDirectory")
	public Path referenceFileBaseDirectory() throws IOException {
		if (applicationContext.getEnvironment().acceptsProfiles("dev", "it", "test")) {
			return configureDirectory(referenceFileBaseDirectory, "reference-file-dev");
		}

		return getExistingPathOrThrow(referenceFileBaseDirectory);
	}

	@Bean(name = "sequenceFileBaseDirectory")
	public Path sequenceFileBaseDirectory() throws IOException {
		if (applicationContext.getEnvironment().acceptsProfiles("dev", "it", "test")) {
			return configureDirectory(sequenceFileBaseDirectory, "sequence-file-dev");
		}
		return getExistingPathOrThrow(sequenceFileBaseDirectory);
	}

	@Bean(name = "outputFileBaseDirectory")
	public Path outputFileBaseDirectory() throws IOException {
		if (applicationContext.getEnvironment().acceptsProfiles("dev", "it", "test")) {
			return configureDirectory(outputFileBaseDirectory, "output-file-dev");
		}
		return getExistingPathOrThrow(outputFileBaseDirectory);
	}

	private Path getExistingPathOrThrow(String directory) {
		Path baseDirectory = Paths.get(directory);
		if (!Files.exists(baseDirectory)) {
			throw new IllegalStateException(
					String.format("Cannot continue startup; base directory [%s] does not exist!",
							baseDirectory.toString()));
		} else {
			logger.info(String.format(
					"Using specified existing directory at [%s]. The directory *will not* be removed at shutdown time.",
					baseDirectory.toString()));
		}
		return baseDirectory;
	}

	private Path configureDirectory(String pathName, String defaultDevPathPrefix) throws IOException {
		Path baseDirectory = Paths.get(pathName);
		if (!Files.exists(baseDirectory)) {
			baseDirectory = Files.createDirectories(baseDirectory);
			logger.info(String.format(
					"The directory [%s] does not exist, but it looks like you're running in a dev environment, "
							+ "so I created a temporary location at [%s]. This directory *may* be removed at shutdown time.",
					pathName, baseDirectory.toString()));
		}
		return baseDirectory;
	}
}
