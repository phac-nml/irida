package ca.corefacility.bioinformatics.irida.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;

/**
 * A repository to store information about sequence files. This repository will
 * not directly store the file, just metadata
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */

public interface SequenceFileRepository extends PagingAndSortingRepository<SequenceFile, Long> {

}
