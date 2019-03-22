package ca.corefacility.bioinformatics.irida.model.run;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "generic_sequencing_run")
@Audited
public class GenericSequencingRun extends SequencingRun {

	private String sequencerType;

	// Key/value map of additional properties you could set on a sequence file.
	// This may contain optional sequencer specific properties.
	@ElementCollection(fetch = FetchType.EAGER)
	@MapKeyColumn(name = "property_key", nullable = false)
	@Column(name = "property_value", nullable = false)
	@CollectionTable(name = "sequencing_run_properties", joinColumns = @JoinColumn(name = "sequencing_run_id"), uniqueConstraints = @UniqueConstraint(columnNames = {
			"sequencing_run_id", "property_key" }, name = "UK_SEQUENCING_RUN_PROPERTY_KEY"))
	private Map<String, String> optionalProperties;

	public GenericSequencingRun() {
		optionalProperties = new HashMap<>();
	}

	public GenericSequencingRun(String sequencerType) {
		this.sequencerType = sequencerType;
	}

	@Override
	public String getSequencerType() {
		return sequencerType;
	}

	public void setSequencerType(String sequencerType) {
		this.sequencerType = sequencerType;
	}

	/**
	 * Add one optional property to the map of properties
	 *
	 * @param key   The key of the property to add
	 * @param value The value of the property to add
	 */
	@JsonAnySetter
	public void addOptionalProperty(String key, String value) {
		optionalProperties.put(key, value);
	}

	/**
	 * Get the Map of optional properties
	 *
	 * @return A {@code Map<String,String>} of all the optional propertie
	 */
	@JsonAnyGetter
	public Map<String, String> getOptionalProperties() {
		return optionalProperties;
	}

	/**
	 * Get an individual optional property
	 *
	 * @param key The key of the property to read
	 * @return A String of the property's value
	 */
	public String getOptionalProperty(String key) {
		return optionalProperties.get(key);
	}

	@Override
	public int compareTo(SequencingRun sequencingRun) {
		if (getSequencerType().equals(sequencingRun.getSequencerType())) {
			return 0;
		}
		return 1;
	}
}
