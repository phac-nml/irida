package ca.corefacility.bioinformatics.irida.service.export;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.NcbiExportSubmission;
import ca.corefacility.bioinformatics.irida.model.enums.ExportUploadState;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.service.CRUDService;

/**
 * Service for exporting {@link SequenceFile} data to NCBI
 */
public interface NcbiExportSubmissionService extends CRUDService<Long, NcbiExportSubmission> {
	public List<NcbiExportSubmission> getSubmissionsWithState(ExportUploadState state);
}
