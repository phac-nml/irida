package ca.corefacility.bioinformatics.irida.repositories.filesystem;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * A repository class for managing {@link SequenceFile} files on the file
 * system.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class SequenceFileFilesystemRepository implements CRUDRepository<Long, SequenceFile> {

    private static final Logger logger = LoggerFactory.getLogger(SequenceFileFilesystemRepository.class);
    private final Path BASE_DIRECTORY;

    public SequenceFileFilesystemRepository(String baseDirectory) {
        this(FileSystems.getDefault().getPath(baseDirectory));
    }

    public SequenceFileFilesystemRepository(Path baseDirectory) {
        this.BASE_DIRECTORY = baseDirectory;
    }

    /**
     * Get the appropriate directory for the {@link SequenceFile}.
     *
     * @param id the {@link Identifier} of the {@link SequenceFile}.
     * @return the {@link Path} for the {@link SequenceFile}.
     */
    private Path getSequenceFileDir(Long id) {
        return BASE_DIRECTORY.resolve(id.toString());
    }
	
	private Path getSequenceFileDirWithRevision(Path sequenceBaseDir, Long fileRevisionNumber){
		return sequenceBaseDir.resolve(fileRevisionNumber.toString());
	}

    /**
     * The {@link SequenceFile} *must* have an identifier before being passed to
     * this method, because the identifier is used as an internal directory
     * name.
     *
     * @param object the {@link SequenceFile} to store.
     * @return a reference to the {@link SequenceFile} with the stored path.
     * @throws IllegalArgumentException if the {@link SequenceFile} does not
     *                                  have an identifier.
     */
    @Override
    public SequenceFile create(SequenceFile object) throws IllegalArgumentException {
        if (object.getId()== null) {
            throw new IllegalArgumentException("Identifier is required.");
        }
        Path sequenceFileDir = getSequenceFileDir(object.getId());
		Path sequenceFileDirWithRevision = getSequenceFileDirWithRevision(sequenceFileDir, object.getFileRevisionNumber());
        Path target = sequenceFileDirWithRevision.resolve(object.getFile().getFileName());
        try {
			if(!Files.exists(sequenceFileDir)){
				Files.createDirectory(sequenceFileDir);
                logger.debug("Created directory: [" + sequenceFileDir.toString() + "]");
			}
        	if (!Files.exists(sequenceFileDirWithRevision)) {
        		Files.createDirectory(sequenceFileDirWithRevision);
                logger.debug("Created directory: [" + sequenceFileDirWithRevision.toString() + "]");
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
     * This method is not supported by {@link SequenceFileFilesystemRepository}
     * and will throw an {@link UnsupportedOperationException}.
     *
     * @param id the file to load.
     * @return the {@link SequenceFile} with a reference to the file.
     * @throws EntityNotFoundException if the file cannot be found.
     * @see ca.corefacility.bioinformatics.irida.repositories.sesame.SequenceFileSesameRepository
     */
    @Override
    public SequenceFile read(Long id) throws EntityNotFoundException {
        throw new UnsupportedOperationException("Files should be loaded by relational repository.");
    }

    private Path updateFilesystemFile(Long id, Path object, Long fileRevisionNumber) throws IllegalArgumentException {
        if (id == null) {
            throw new IllegalArgumentException("Identifier is required.");
        }

        // the sequence file directory must previously exist, if not, then
        // we're doing something very weird.
        Path sequenceFileDir = getSequenceFileDir(id);
		Path sequenceFileDirWithRevision = getSequenceFileDirWithRevision(sequenceFileDir, fileRevisionNumber);

        if (!Files.exists(sequenceFileDir)) {
            throw new IllegalArgumentException("The directory for this "
                    + "SequenceFile does not exist, has it been persisted "
                    + "before?");
        }
		
		if(Files.exists(sequenceFileDirWithRevision)){
			throw new IllegalArgumentException("The directory for this sequence file revision already exists.");
		}

        Path target = sequenceFileDirWithRevision.resolve(object.getFileName());

        // now handle storing the file as before:
        try {
			Files.createDirectory(sequenceFileDirWithRevision);
            Files.move(object, target);
        } catch (IOException e) {
            throw new StorageException("Couldn't move updated file to existing directory.");
        }

        return target;
    }

    @Override
    public List<SequenceFile> list() {
        throw new UnsupportedOperationException("Files cannot be listed independently.");
    }

    @Override
    public List<SequenceFile> list(int page, int size, String sortProperty, Order order) {
        throw new UnsupportedOperationException("Files cannot be listed independently.");
    }

    @Override
    public Integer count() {
        throw new UnsupportedOperationException("Files cannot be counted");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SequenceFile update(Long id, Map<String, Object> updatedFields) throws InvalidPropertyException, SecurityException {
        SequenceFile file = new SequenceFile();
		
		if(!updatedFields.containsKey("fileRevisionNumber")){
			throw new IllegalArgumentException("Cannot update of sequence file without file revision number");
		}
		
		Long fileRevisionNumber = (Long) updatedFields.get("fileRevisionNumber");
		
        if (updatedFields.containsKey("file")) {
            Path updatedFile = (Path) updatedFields.get("file");
            Path target = updateFilesystemFile(id, updatedFile,fileRevisionNumber);
            file.setFile(target);
        }

        return file;
    }

    @Override
    public void delete(Long id) throws EntityNotFoundException {
        throw new UnsupportedOperationException("Files cannot be deleted.");

    }

    @Override
    public Boolean exists(Long id) {
        throw new UnsupportedOperationException("SequenceFile file exists "
                + "should be populated by SequenceFileRelationalRepository.");
    }

    /**
     * Reading multiple files will not be supported
     */
    @Override
    public Collection<SequenceFile> readMultiple(Collection<Long> idents) {
        throw new UnsupportedOperationException("Reading multiple files will not be supported.");
    }

	@Override
	public List<SequenceFile> listAll() {
		throw new UnsupportedOperationException("Files cannot be listed independently.");
	}

}
