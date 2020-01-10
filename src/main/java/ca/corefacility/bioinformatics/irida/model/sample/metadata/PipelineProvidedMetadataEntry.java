package ca.corefacility.bioinformatics.irida.model.sample.metadata;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import org.hibernate.envers.Audited;

import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * {@link MetadataEntry} that has been created by an analysis pipeline
 */
@Entity
@Audited
@Table(name = "pipeline_metadata_entry")
public class PipelineProvidedMetadataEntry extends MetadataEntry {

	@ManyToOne
	private AnalysisSubmission submission;

	//private constructor for hibernate
	@SuppressWarnings("unused")
	private PipelineProvidedMetadataEntry() {
	}

	/**
	 * Build a {@link PipelineProvidedMetadataEntry} with the given value, type and {@link AnalysisSubmission}
	 *
	 * @param value      the value of the metadata entry
	 * @param type       the datatype of the metadata
	 * @param submission the {@link AnalysisSubmission} that created this metadata
	 */
	public PipelineProvidedMetadataEntry(String value, String type, AnalysisSubmission submission) {
		super(value, type);
		this.submission = submission;
	}

	/**
	 * Get the {@link AnalysisSubmission} that created this metadata
	 *
	 * @return the {@link AnalysisSubmission}
	 */
	public AnalysisSubmission getSubmission() {
		return submission;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void merge(MetadataEntry metadataEntry) {
		super.merge(metadataEntry);

		PipelineProvidedMetadataEntry pipelineMetadataEntry = (PipelineProvidedMetadataEntry) metadataEntry;

		this.submission = pipelineMetadataEntry.getSubmission();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PipelineProvidedMetadataEntry) {
			return super.equals(obj) && Objects.equals(submission, ((PipelineProvidedMetadataEntry) obj).submission);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), submission);
	}
}
