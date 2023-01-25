package ca.corefacility.bioinformatics.irida.repositories.filesystem;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.persistence.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.VersionedFileFields;

/**
 * Custom implementation of a repository that writes the {@link Path} part of an entity to disk.
 *
 * @param <Type> The type of object this repository is storing
 */
public abstract class FilesystemSupplementedRepositoryImpl<Type extends VersionedFileFields<Long> & IridaThing>
		implements FilesystemSupplementedRepository<Type> {

	private static final Logger logger = LoggerFactory.getLogger(FilesystemSupplementedRepository.class);

	private final Path baseDirectory;
	private final EntityManager entityManager;

	private IridaFileStorageUtility iridaFileStorageUtility;

	public FilesystemSupplementedRepositoryImpl(final EntityManager entityManager, final Path baseDirectory,
			final IridaFileStorageUtility iridaFileStorageUtility) {
		this.entityManager = entityManager;
		this.baseDirectory = baseDirectory;
		this.iridaFileStorageUtility = iridaFileStorageUtility;
	}

	/**
	 * A JPA event listener to translate the relative paths stored in the database to absolute paths so that everyone
	 * after the repository knows where the file is actually stored.
	 */
	public static class RelativePathTranslatorListener {
		private static final Logger logger = LoggerFactory.getLogger(RelativePathTranslatorListener.class);

		private static final Map<Class<?>, Path> baseDirectories = new ConcurrentHashMap<>();

		private static final Predicate<Field> pathFilter = f -> f.getType().equals(Path.class);

		/**
		 * Get a collection of fields that have type Path.
		 *
		 * @param type the class type to get field references for.
		 * @return the set of field references for the class.
		 */
		private static Set<Field> findPathFields(final Class<?> type) {
			return Arrays.stream(type.getDeclaredFields()).filter(pathFilter).collect(Collectors.toSet());
		}

		/**
		 * Add a base directory to safe files to
		 *
		 * @param c The class for the base directory to save files
		 * @param p the path to save files to
		 */
		public static void addBaseDirectory(final Class<?> c, final Path p) {
			baseDirectories.put(c, p);
		}

		/**
		 * Whenever a {@link VersionedFileFields} is loaded from the database, we need to translate it's path from a
		 * relative path to an absolute path based on the storage directory for the type.
		 *
		 * @param fileSystemEntity the object to make absolute paths for
		 */
		@PostLoad
		@PostUpdate
		@PostPersist
		public void absolutePath(final VersionedFileFields<Long> fileSystemEntity) {
			logger.trace("Going to get an absolute path after loading.");
			final Path directoryForType = baseDirectories.get(fileSystemEntity.getClass());
			// find any members that are of type Path:
			final Set<Field> pathFields = findPathFields(fileSystemEntity.getClass());

			// for every member that's a path, make it an absolute path based on
			// the
			// base directory
			for (final Field field : pathFields) {
				ReflectionUtils.makeAccessible(field);
				final Path source = (Path) ReflectionUtils.getField(field, fileSystemEntity);
				// source will have a null root **only** if it's a relative
				// path. basically: don't try to make an absolute path out of
				// one that's already absolute.
				if (source != null && source.getRoot() == null) {
					logger.trace("About to get ABSOLUTE path for [" + source.toString() + "] from base directory ["
							+ directoryForType.toString() + "]");
					final Path absolutePath = directoryForType.resolve(source);
					ReflectionUtils.setField(field, fileSystemEntity, absolutePath);
					logger.trace("Setting ABSOLUTE path to [" + absolutePath.toString() + "] from relative path ["
							+ source.toString() + "]");
				} else {
					logger.trace("Not translating file path for file: " + source);
				}
			}
		}

		/**
		 * Before persisting a {@link VersionedFileFields} to the database, we need to translate it to a relative path
		 * by stripping the storage directory for the type.
		 *
		 * @param fileSystemEntity the object to make relative paths for.
		 */
		@PreUpdate
		public void relativePath(final VersionedFileFields<Long> fileSystemEntity) {
			logger.trace("In pre-update, going to translate to relative path.");

			final Path directoryForType = baseDirectories.get(fileSystemEntity.getClass());
			// find any members that are of type Path:
			final Set<Field> pathFields = findPathFields(fileSystemEntity.getClass());

			// for every member that's a path, make it a relative path based on
			// the
			// base directory
			for (final Field field : pathFields) {
				ReflectionUtils.makeAccessible(field);
				final Path source = (Path) ReflectionUtils.getField(field, fileSystemEntity);
				// source will have a not-null root **only** if it's an absolute
				// path.
				if (source != null && source.getRoot() != null) {
					logger.trace("About to get RELATIVE path for [" + source.toString() + "] from base directory ["
							+ directoryForType.toString() + "]");
					final Path relativePath = directoryForType.relativize(source);
					ReflectionUtils.setField(field, fileSystemEntity, relativePath);
					logger.trace("Setting RELATIVE path to [" + relativePath.toString() + "] from absolute path ["
							+ source.toString() + "]");
				}
			}
		}
	}

	/**
	 * Actually persist the entity to disk and to the database.
	 *
	 * @param entity the entity to persist.
	 * @return the persisted entity.
	 */
	protected Type saveInternal(final Type entity) {
		logger.trace("In write internal, before doing any persisting.");
		if (entity.getId() == null) {
			logger.trace("file has never been saved before, writing to database.");
			// save the initial version of the file to the database so that we
			// get an identifier attached to it.
			entityManager.persist(entity);
		}
		logger.trace("About to write files to disk.");

		writeFilesToDisk(baseDirectory, entity);

		logger.trace("Returning merged entity.");
		return entityManager.merge(entity);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public Type saveMetadata(final Type entity) {
		logger.trace("Saving entity state without any file changes");

		return entityManager.merge(entity);
	}

	/**
	 * Persist an entity to disk and database. Implementors of this method are recommended to call
	 * {@link FilesystemSupplementedRepositoryImpl#saveInternal} to avoid repeated boilerplate code.
	 *
	 * @param entity the entity to persist.
	 * @return the persisted entity.
	 */
	public abstract Type save(final Type entity);

	/**
	 * Write any files to disk and update the {@link Path} location. This method works using reflection to automagically
	 * find and update any internal {@link Path} members on the {@link VersionedFileFields}. This class **does not**
	 * update the object in the database
	 *
	 * @param baseDirectory
	 * @param objectToWrite
	 */
	private void writeFilesToDisk(Path baseDirectory, Type objectToWrite) {
		if (objectToWrite.getId() == null) {
			throw new IllegalArgumentException("Identifier is required.");
		}

		Path sequenceFileDir = baseDirectory.resolve(objectToWrite.getId().toString());

		Predicate<Field> pathFilter = f -> f.getType().equals(Path.class);
		// now find any members that are of type Path and shuffle them around:

		Set<Field> pathFields = Arrays.stream(objectToWrite.getClass().getDeclaredFields())
				.filter(pathFilter)
				.collect(Collectors.toSet());

		Set<Field> fieldsToUpdate = new HashSet<>();
		for (Field field : pathFields) {
			ReflectionUtils.makeAccessible(field);
			Path source = (Path) ReflectionUtils.getField(field, objectToWrite);
			if (source != null) {
				fieldsToUpdate.add(field);
			}
		}

		// if there are non-null fields, increment the revision number and
		// update the objects
		if (!fieldsToUpdate.isEmpty()) {
			objectToWrite.incrementFileRevisionNumber();
			Path sequenceFileDirWithRevision = sequenceFileDir.resolve(
					objectToWrite.getFileRevisionNumber().toString());

			for (Field field : fieldsToUpdate) {
				Path source = (Path) ReflectionUtils.getField(field, objectToWrite);
				Path target = sequenceFileDirWithRevision.resolve(source.getFileName());
				logger.debug("Target is [" + target.toString() + "]");
				iridaFileStorageUtility.writeFile(source, target, sequenceFileDir, sequenceFileDirWithRevision);
				ReflectionUtils.setField(field, objectToWrite, target);
			}
		}
	}

}
