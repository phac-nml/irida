package ca.corefacility.bioinformatics.irida.repositories;

import ca.corefacility.bioinformatics.irida.model.NcbiExportSubmission;

/**
 * Repository for storing and reading {@link NcbiExportSubmission}s
 */
public interface NcbiExportSubmissionRepository extends IridaJpaRepository<NcbiExportSubmission, Long> {

}
