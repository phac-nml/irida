package ca.corefacility.bioinformatics.irida.ria.components;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;

import com.google.common.collect.Lists;

/**
 * Component for handling data needed for pipeline submission
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class PipelineSubmission {

	private ReferenceFile referenceFile;
	private Set<SequenceFile> sequenceFiles;

	@Autowired
	public PipelineSubmission() {
		this.sequenceFiles = new HashSet<>();
	}

	/**
	 * Set the reference file (if applicable) for the pipeline submission.
	 *
	 * @param referenceFile {@link ReferenceFile}
	 */
	public void setReferenceFile(ReferenceFile referenceFile) {
		this.referenceFile = referenceFile;
	}

	/**
	 * Set the sequence files to run the pipeline on.
	 *
	 * @param files Iterable list of {@link SequenceFile}
	 */
	public void setSequenceFiles(Iterable<SequenceFile> files) {
		sequenceFiles.addAll(Lists.newArrayList(files));
	}

	public ReferenceFile getReferenceFile() {
		return referenceFile;
	}

	public Set<SequenceFile> getSequenceFiles() {
		return sequenceFiles;
	}

	/**
	 * Reset the submission for the next time.
	 */
	public void clear() {
		this.referenceFile = null;
		this.sequenceFiles.clear();
	}
}
