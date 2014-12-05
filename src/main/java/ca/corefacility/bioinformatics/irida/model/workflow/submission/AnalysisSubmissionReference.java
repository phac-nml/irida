package ca.corefacility.bioinformatics.irida.model.workflow.submission;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;

/**
 * Defines an analysis submission which requires a reference file as input.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@Entity
@Table(name = "analysis_submission_reference")
@Audited
public class AnalysisSubmissionReference extends AnalysisSubmission {
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "reference_file_id")
	private ReferenceFile referenceFile;

	/**
	 * Builds a new {@link AnalysisSubmissionReference} with the given
	 * information.
	 * 
	 * @param name
	 *            The name of the submission.
	 * @param inputFiles
	 *            The set of input {@link SequenceFile}s.
	 * @param inputReferenceFile
	 *            The input {@link ReferenceFile}.
	 * @param workflowId
	 *            The id of the implementing workflow.
	 */
	public AnalysisSubmissionReference(String name, Set<SequenceFile> inputFiles, ReferenceFile inputReferenceFile,
			UUID workflowId) {
		super(name, inputFiles, workflowId);
		checkNotNull(referenceFile, "referenceFile is null");

		this.referenceFile = inputReferenceFile;
	}

	/**
	 * Sets the reference file.
	 * 
	 * @param referenceFile
	 *            The reference file.
	 */
	public void setReferenceFile(ReferenceFile referenceFile) {
		this.referenceFile = referenceFile;
	}

	/**
	 * Gets the ReferenceFile.
	 * 
	 * @return The ReferenceFile.
	 */
	public ReferenceFile getReferenceFile() {
		return referenceFile;
	}

	@Override
	public String toString() {
		return "AnalysisSubmissionReference [referenceFile=" + referenceFile + ", toString()=" + super.toString() + "]";
	}
}
