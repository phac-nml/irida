package ca.corefacility.bioinformatics.irida.model.sequenceFile;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ca.corefacility.bioinformatics.irida.model.IridaResourceSupport;
import ca.corefacility.bioinformatics.irida.model.MutableIridaThing;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;

/**
 * Objects that were obtained from some sequencing platform.
 */
@Entity
@Table(name = "sequencing_object")
@EntityListeners(AuditingEntityListener.class)
@Audited
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class SequencingObject extends IridaResourceSupport implements MutableIridaThing {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date")
	private Date createdDate;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "sequencing_run_id")
	private SequencingRun sequencingRun;

	public SequencingObject() {
		createdDate = new Date();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	@Override
	public Date getModifiedDate() {
		return createdDate;
	}

	@JsonIgnore
	public SequencingRun getSequencingRun() {
		return sequencingRun;
	}

	@JsonIgnore
	public void setSequencingRun(SequencingRun sequencingRun) {
		this.sequencingRun = sequencingRun;
	}

	/**
	 * Get the {@link SequenceFile}s associated with this
	 * {@link SequencingObject}
	 * 
	 * @return a Set of {@link SequenceFile}
	 */
	public abstract Set<SequenceFile> getFiles();
}
