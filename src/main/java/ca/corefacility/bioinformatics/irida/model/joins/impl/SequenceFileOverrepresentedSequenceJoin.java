package ca.corefacility.bioinformatics.irida.model.joins.impl;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.envers.Audited;

import ca.corefacility.bioinformatics.irida.model.OverrepresentedSequence;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;

@Entity
@Table(name = "sequencefile_overrepresentedsequence")
@Audited
public class SequenceFileOverrepresentedSequenceJoin implements Join<SequenceFile, OverrepresentedSequence> {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;

	@ManyToOne
	@JoinColumn(name = "sequencefile_id")
	private SequenceFile sequenceFile;

	@ManyToOne
	@JoinColumn(name = "overrepresentedsequence_id")
	private OverrepresentedSequence sequence;

	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;

	private SequenceFileOverrepresentedSequenceJoin() {
		timestamp = new Date();
	}
	
	public SequenceFileOverrepresentedSequenceJoin(SequenceFile s, OverrepresentedSequence os) {
		this();
		this.sequenceFile = s;
		this.sequence = os;
	}
	
	@Override
	public Date getTimestamp() {
		return timestamp;
	}

	@Override
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;

	}

	@Override
	public SequenceFile getSubject() {
		return sequenceFile;
	}

	@Override
	public void setSubject(SequenceFile subject) {
		this.sequenceFile = subject;
	}

	@Override
	public OverrepresentedSequence getObject() {
		return sequence;
	}

	@Override
	public void setObject(OverrepresentedSequence object) {
		this.sequence = object;
	}

}
