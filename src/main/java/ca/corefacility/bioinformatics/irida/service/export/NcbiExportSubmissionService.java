package ca.corefacility.bioinformatics.irida.service.export;

import ca.corefacility.bioinformatics.irida.model.NcbiExportSubmission;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.service.CRUDService;

/**
 * Service for exporting {@link SequenceFile} data to NCBI
 */
public interface NcbiExportSubmissionService extends CRUDService<Long, NcbiExportSubmission> {

}
