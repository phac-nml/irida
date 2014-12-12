package ca.corefacility.bioinformatics.irida.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;

/**
 * Test sequencing run entity
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
@Entity
@Table(name = "sequencing_run_entity")
@Audited
public class SequencingRunEntity extends SequencingRun {
	private String data;

	public SequencingRunEntity() {
		super();
	}

	public SequencingRunEntity(String data) {
		this();
		this.data = data;
	}

	/**
	 * @return the data
	 */
	public String getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(String data) {
		this.data = data;
	}

	@Override
	public String getSequencerType() {
		return "TestSequencer";
	}

}
