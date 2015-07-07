package ca.corefacility.bioinformatics.irida.service;

import ca.corefacility.bioinformatics.irida.model.NcbiExportSubmission;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;

/**
 * Service for exporting {@link SequenceFile} data to NCBI
 */
public interface NcbiExportSubmissionService extends CRUDService<Long, NcbiExportSubmission> {

}
