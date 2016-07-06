package ca.corefacility.bioinformatics.irida.repositories.sequencefile;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.persistence.PostLoad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ReflectionUtils;

import ca.corefacility.bioinformatics.irida.model.VersionedFileFields;

/**
 * This class is used to translate relative paths to absolute paths.
 */
public class FilesystemPathListener implements ApplicationContextAware {

	private static final Logger logger = LoggerFactory.getLogger(FilesystemPathListener.class);
	private static final Predicate<Field> pathFilter = f -> f.getType().equals(Path.class);
	private static ApplicationContext applicationContext;

	/**
	 * Get a collection of fields that have type Path.
	 * 
	 * @param type
	 *            the class type to get field references for.
	 * @return the set of field references for the class.
	 */
	private static Set<Field> findPathFields(final Class<?> type) {
		return Arrays.stream(type.getDeclaredFields()).filter(pathFilter).collect(Collectors.toSet());
	}
	
	private static Path baseDirectory() {
		return (Path) applicationContext.getBean("sequenceFileBaseDirectory");
	}

	//@PrePersist
	//@PreUpdate
	public void relativizePath(final VersionedFileFields<Long> fileSystemEntity) {
		logger.debug("Going to relativize path before persisting.");
		final Set<Field> pathFields = findPathFields(fileSystemEntity.getClass());

		// for every member that's a path, make it an absolute path based on the
		// base directory
		for (final Field field : pathFields) {
			ReflectionUtils.makeAccessible(field);
			final Path source = (Path) ReflectionUtils.getField(field, fileSystemEntity);
			if (source != null) {
				final Path relativePath = baseDirectory().relativize(source);
				ReflectionUtils.setField(field, fileSystemEntity, relativePath);
			}
		}
	}

	@PostLoad
	public void absolutePath(final VersionedFileFields<Long> fileSystemEntity) {
		logger.debug("Going to get an absolute path after loading.");
		// now find any members that are of type Path:
		final Set<Field> pathFields = findPathFields(fileSystemEntity.getClass());

		// for every member that's a path, make it an absolute path based on the
		// base directory
		for (final Field field : pathFields) {
			ReflectionUtils.makeAccessible(field);
			final Path source = (Path) ReflectionUtils.getField(field, fileSystemEntity);
			if (source != null) {
				final Path absolutePath = baseDirectory().resolve(source);
				ReflectionUtils.setField(field, fileSystemEntity, absolutePath);
			}
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		FilesystemPathListener.applicationContext = applicationContext;
	}
}
