package ca.corefacility.bioinformatics.irida.model.joins.impl;

import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.envers.Audited;

/**
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Entity
@Table(name = "sequencefile_sample")
@Audited
public class SampleSequenceFileJoin implements Join<Sample, SequenceFile> {

	public SampleSequenceFileJoin() {
		createdDate = new Date();
	}

	public SampleSequenceFileJoin(Sample subject, SequenceFile object) {
		this.sequenceFile = object;
		this.sample = subject;
		createdDate = new Date();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;

	@ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.DETACH) 
	@JoinColumn(name = "sequencefile_id")
	private SequenceFile sequenceFile;

	@ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.DETACH) 
	@JoinColumn(name = "sample_id")
	private Sample sample;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@Override
	public Sample getSubject() {
		return sample;
	}

	@Override
	public void setSubject(Sample subject) {
		this.sample = subject;
	}

	@Override
	public SequenceFile getObject() {
		return sequenceFile;
	}

	@Override
	public void setObject(SequenceFile object) {
		this.sequenceFile = object;
	}

	@Override
	public Date getTimestamp() {
		return createdDate;
	}

	@Override
	public void setTimestamp(Date timestamp) {
		this.createdDate = timestamp;
	}
}
