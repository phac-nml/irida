package ca.corefacility.bioinformatics.irida.model.sample.metadata;

import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Audited
@Table(name = "pipeline_metadata_entry")
public class PipelineProvidedMetadataEntry extends MetadataEntry {
	@ManyToOne
	private AnalysisSubmission submission;

	//private constructor for hibernate
	private PipelineProvidedMetadataEntry() {

	}

	public PipelineProvidedMetadataEntry(String value, String type, AnalysisSubmission submission) {
		super(value, type);
		this.submission = submission;
	}

	public AnalysisSubmission getSubmission() {
		return submission;
	}
}
