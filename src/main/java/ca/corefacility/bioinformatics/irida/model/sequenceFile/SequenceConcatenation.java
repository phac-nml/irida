package ca.corefacility.bioinformatics.irida.model.sequenceFile;

import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.IridaThing;

/**
 * Class storing when 2 {@link SequencingObject}s are concatenated into a new {@link SequencingObject}
 */
@Entity
@Table(name = "sequence_concatenation")
@EntityListeners(AuditingEntityListener.class)
public class SequenceConcatenation implements IridaThing {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date", updatable = false)
	private Date createdDate;

	@ManyToMany
	@JoinTable(joinColumns = @JoinColumn(name = "sequence_concatenation_id"))
	@NotNull
	private final List<SequencingObject> sources;

	@OneToOne
	@NotNull
	private final SequencingObject concatenated;

	/**
	 * Default constructor needed by Hibernate.
	 */
	public SequenceConcatenation() {
		this.sources = null;
		this.concatenated = null;
	}

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
