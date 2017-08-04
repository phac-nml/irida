package ca.corefacility.bioinformatics.irida.model.sequenceFile;

import java.util.Date;
import java.util.List;

import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.IridaThing;

/**
 * Class storing when 2 {@link SequencingObject}s are concatenated into a new
 * {@link SequencingObject}
 */
@Entity
@Table(name = "sequence_concatenation")
@EntityListeners(AuditingEntityListener.class)
public class SequenceConcatenation implements IridaThing {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date")
	private Date createdDate;

	@ManyToMany
	@NotNull
	private final List<SequencingObject> sources;

	@OneToOne
	@NotNull
	private final SequencingObject concatenated;

	public SequenceConcatenation(SequencingObject concatenated, List<SequencingObject> sources) {
		this.concatenated = concatenated;
		this.sources = sources;
	}

	@Override
	public Date getCreatedDate() {
		return createdDate;
	}

	@Override
	public String getLabel() {
		return "Concatenation " + id;
	}

	@Override
	public Long getId() {
		return id;
	}

}
