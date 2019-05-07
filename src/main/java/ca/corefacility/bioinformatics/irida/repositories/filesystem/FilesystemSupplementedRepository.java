package ca.corefacility.bioinformatics.irida.repositories.filesystem;

import java.nio.file.Path;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.VersionedFileFields;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;

/**
 * Custom implementation interface for writing the {@link Path} part of a
 * {@link SequenceFile} to disk. The interface is left intentionally empty --
 * the custom implementation re-implements only the persisting methods defined
 * as {@link CrudRepository#save(Object)}.
 * 
 *	@param <Type> The type of object this repository is storing
 */
@NoRepositoryBean
public interface FilesystemSupplementedRepository<Type extends VersionedFileFields<Long> & IridaThing> {
	/**
	 * Save an entity without updating any of the {@link Path} entries within
	 * the file. WARNING, if you update the {@link Path} entries of this file
	 * and use this method, a new file revision will not be created in the file
	 * storage or database.
	 * 
	 * @param entity
	 *            the entity to save
	 * @return the saved entity
	 */
	public Type saveMetadata(final Type entity);
}