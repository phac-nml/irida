package ca.corefacility.bioinformatics.irida.repositories.filesystem;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileFilesystem;

/**
 * A repository class for managing {@link SequenceFile} files on the file
 * system.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class SequenceFileFilesystemImpl implements SequenceFileFilesystem {

	private static final Logger logger = LoggerFactory.getLogger(SequenceFileFilesystemImpl.class);
	private final Path BASE_DIRECTORY;

	public SequenceFileFilesystemImpl(String baseDirectory) {
		this(FileSystems.getDefault().getPath(baseDirectory));
	}

	public SequenceFileFilesystemImpl(Path baseDirectory) {
		this.BASE_DIRECTORY = baseDirectory;
	}

	/**
	 * Get the appropriate directory for the {@link SequenceFile}.
	 * 
	 * @param id
	 *            the identifier of the {@link SequenceFile}.
	 * @return the {@link Path} for the {@link SequenceFile}.
	 */
	private Path getSequenceFileDir(Long id) {
		return BASE_DIRECTORY.resolve(id.toString());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SequenceFile writeSequenceFileToDisk(SequenceFile object) throws IllegalArgumentException {
		if (object.getId() == null) {
			throw new IllegalArgumentException("Identifier is required.");
		}
		Path sequenceFileDir = getSequenceFileDir(object.getId());
		Path target = sequenceFileDir.resolve(object.getFile().getFileName());
		try {
			if (!Files.exists(sequenceFileDir)) {
				Files.createDirectory(sequenceFileDir);
				logger.debug("Created directory: [" + sequenceFileDir.toString() + "]");
			}
			Files.move(object.getFile(), target);
		} catch (IOException e) {
			e.printStackTrace();
			throw new StorageException("Failed to move file into new directory.");
		}
		object.setFile(target);
		return object;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Path updateSequenceFileOnDisk(Long id, Path updatedFile) throws IllegalArgumentException, StorageException {
		if (id == null) {
			throw new IllegalArgumentException("Identifier is required.");
		}

		// the sequence file directory must previously exist, if not, then
		// we're doing something very weird.
		Path sequenceFileDir = getSequenceFileDir(id);
		if (!Files.exists(sequenceFileDir)) {
			throw new IllegalArgumentException("The directory for this "
					+ "SequenceFile does not exist, has it been persisted " + "before?");
		}

		// the directory exists. does the target file exist? if so, we don't
		// want to overwrite the file. We'll rename the existing file with the
		// current date appended to the end so that we're retaining existing
		// file names.
		Path target = sequenceFileDir.resolve(updatedFile.getFileName());
		if (Files.exists(target)) {
			String destination = target.getFileName().toString() + "-" + new Date().getTime();

			// rename the existing file
			try {
				Files.move(target, target.resolveSibling(destination));
			} catch (IOException e) {
				throw new StorageException("Couldn't rename existing file.");
			}
		}

		// now handle storing the file as before:
		try {
			Files.move(updatedFile, target);
		} catch (IOException e) {
			throw new StorageException("Couldn't move updated file to existing directory.");
		}

		return target;
	}
}
