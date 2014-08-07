package ca.corefacility.bioinformatics.irida.repositories.filesystem;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.model.VersionedFileFields;

/**
 * Responsible for persisting {@link Path} to a disk.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public abstract class PathRepository {

	private static final Logger logger = LoggerFactory.getLogger(PathRepository.class);

	/**
	 * Write any files to disk and update the {@link Path} location. This method
	 * works using reflection to automagically find and update any internal
	 * {@link Path} members on the {@link VersionedFileFields}. This class
	 * **does not** update the object in the database
	 * 
	 * @param baseDirectory
	 * @param iridaThing
	 */
	public static VersionedFileFields<?> writeFilesToDisk(Path baseDirectory, VersionedFileFields<?> objectToWrite) {
		if (objectToWrite.getId() == null) {
			throw new IllegalArgumentException("Identifier is required.");
		}
		objectToWrite.modifyFileRevisionNumber();
		
		Path sequenceFileDir = baseDirectory.resolve(objectToWrite.getId().toString());
		Path sequenceFileDirWithRevision = sequenceFileDir.resolve(objectToWrite.getFileRevisionNumber().toString());

		// now find any members that are of type Path and shuffle them around:
		Set<Field> pathFields = Arrays.stream(objectToWrite.getClass().getDeclaredFields())
				.filter(f -> f.getType().equals(Path.class)).collect(Collectors.toSet());

		for (Field field : pathFields) {
			ReflectionUtils.makeAccessible(field);
			Path source = (Path) ReflectionUtils.getField(field, objectToWrite);
			Path target = sequenceFileDirWithRevision.resolve(source.getFileName());
			try {
				if (!Files.exists(sequenceFileDir)) {
					Files.createDirectory(sequenceFileDir);
					logger.trace("Created directory: [" + sequenceFileDir.toString() + "]");
				}

				if (!Files.exists(sequenceFileDirWithRevision)) {
					Files.createDirectory(sequenceFileDirWithRevision);
					logger.trace("Created directory: [" + sequenceFileDirWithRevision.toString() + "]");
				}

				Files.move(source, target);
				logger.trace("Moved file " + source + " to " + target);
			} catch (IOException e) {
				logger.error("Unable to move file into new directory", e);
				throw new StorageException("Failed to move file into new directory.", e);
			}

			ReflectionUtils.setField(field, objectToWrite, target);
		}

		return objectToWrite;
	}
}
