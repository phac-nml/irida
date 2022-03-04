package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableModel;

/**
 * Represent a Sample with Metadata in the UI.
 */
public class LineListTableModel extends TableModel {
	private final Project project;
	private final Boolean owner;
	private final Map<String, String> metadata;

	public LineListTableModel(Project project, Sample sample, Boolean owner, Set<MetadataEntry> metadata) {
		super(sample.getId(), sample.getSampleName(), sample.getCreatedDate(), sample.getModifiedDate());
		this.project = project;
		this.owner = owner;
		this.metadata = getAllMetadataForSample(metadata);
	}

	public String getOwner() {
		return String.valueOf(owner);
	}

	public String getProject() {
		return project.getLabel();
	}

	public Map<String, String> getMetadata() {
		return metadata;
	}

	/**
	 * Convert the sample metadata into a format that can be consumed by the table.
	 *
	 * @param metadataEntries the Metadata entries
	 * @return {@link Map} of {@link String} field and {@link String} value
	 */
	private Map<String, String> getAllMetadataForSample(Set<MetadataEntry> metadataEntries) {
		Map<String, String> entries = new HashMap<>();
		for (MetadataEntry entry : metadataEntries) {
			entries.put(entry.getField().getFieldKey(), entry.getValue());
		}
		return entries;
	}
}
