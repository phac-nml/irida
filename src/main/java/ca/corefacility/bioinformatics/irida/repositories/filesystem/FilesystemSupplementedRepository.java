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
 *
 */
@NoRepositoryBean
public interface FilesystemSupplementedRepository<Type extends VersionedFileFields<Long> & IridaThing> {

	/**
	 * Update an entity without creating a new file revision. This method is
	 * only to be used in cases where any file property is not being updated.
	 * 
	 * @param entity
	 *            The entity to update
	 * @return The updated entity.
	 */
	public Type updateWithoutFileRevision(Type entity);
}