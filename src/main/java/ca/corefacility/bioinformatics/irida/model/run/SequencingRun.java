package ca.corefacility.bioinformatics.irida.model.run;

import java.util.Date;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.enums.SequencingRunUploadStatus;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;

@Entity
@Table(name = "sequencing_run")
@Audited
@Inheritance(strategy = InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
public abstract class SequencingRun implements IridaThing, Comparable<SequencingRun> {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Lob
	private String description;

	@CreatedDate
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private final Date createdDate;

	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE }, mappedBy = "sequencingRun")
	private Set<SequenceFile> sequenceFiles;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "upload_status")
	private SequencingRunUploadStatus uploadStatus;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "layout_type")
	private LayoutType layoutType;

	public SequencingRun() {
		layoutType = LayoutType.SINGLE_END;
		uploadStatus = SequencingRunUploadStatus.UPLOADING;
		createdDate = new Date();
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public Date getCreatedDate() {
		return createdDate;
	}

	@Override
	public Date getModifiedDate() {
		return modifiedDate;
	}

	@Override
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	@Override
	public int compareTo(SequencingRun p) {
		return modifiedDate.compareTo(p.modifiedDate);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLabel() {
		return "SequencingRun " + createdDate;
	}

	@Override
	public int hashCode() {
		return Objects.hash(createdDate, modifiedDate, description);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SequencingRun)) {
			return false;
		}
		final SequencingRun other = (SequencingRun) obj;
		if (Objects.equals(this.description, other.description) && Objects.equals(this.createdDate, other.createdDate)
				&& Objects.equals(this.modifiedDate, other.modifiedDate)) {
			return true;
		}

		return false;
	}

	public void setUploadStatus(SequencingRunUploadStatus uploadStatus) {
		this.uploadStatus = uploadStatus;
	}

	public SequencingRunUploadStatus getUploadStatus() {
		return uploadStatus;
	}

	public LayoutType getLayoutType() {
		return layoutType;
	}

	public void setLayoutType(LayoutType layoutType) {
		this.layoutType = layoutType;
	}

	/**
	 * Get the sequencer type
	 * 
	 * @return Name of the sequencer type
	 */
	public abstract String getSequencerType();

	/**
	 * The type of layout for the run. Single/Paired end
	 * 
	 *
	 */
	public static enum LayoutType {
		SINGLE_END, PAIRED_END
	}

}
