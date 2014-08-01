package ca.corefacility.bioinformatics.irida.repositories.filesystem;

import java.nio.file.Path;

import org.springframework.data.repository.CrudRepository;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;

/**
 * Custom implementation interface for writing the {@link Path} part of a
 * {@link SequenceFile} to disk. The interface is left intentionally empty --
 * the custom implementation re-implements only the persisting methods defined
 * as {@link CrudRepository#save(Object)}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
public interface SequenceFileRepositoryCustom {

}