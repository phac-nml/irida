package ca.corefacility.bioinformatics.irida.model.project.library;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.project.Project;

/**
 * An experiment object to capture experiment metadata about how a
 * {@link SequenceFile} was generated.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
@Entity
@Table(name = "library_description")
@Audited
@EntityListeners(AuditingEntityListener.class)
public class LibraryDescription implements IridaThing {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date", nullable = false)
	private final Date createdDate;

	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modified_date")
	private Date modifiedDate;

	@Lob
	@Column(name = "comment")
	private String comment;

	@Enumerated(EnumType.STRING)
	@Column(name = "source", nullable = false)
	private final Source source;

	@Embedded
	@NotNull
	private final Strategy strategy;

	@Embedded
	@NotNull
	private final Layout layout;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH, optional = false)
	@JoinColumn(name = "project_id")
	private Project project;

	/**
	 * For hibernate.
	 */
	@SuppressWarnings("unused")
	private LibraryDescription() {
		this.createdDate = new Date();
		this.source = null;
		this.strategy = null;
		this.layout = null;
	}

	public LibraryDescription(final Source source, final Strategy strategy, final Layout layout) {
		this.createdDate = new Date();
		this.source = source;
		this.strategy = strategy;
		this.layout = layout;
	}

	@Override
	public Date getCreatedDate() {
		return this.createdDate;
	}

	@Override
	public Date getModifiedDate() {
		return this.modifiedDate;
	}

	@Override
	public void setModifiedDate(final Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	@Override
	public String getLabel() {
		return this.createdDate.toString();
	}

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public void setId(final Long id) {
		this.id = id;
	}

	public String getComment() {
		return comment;
	}

	public Source getSource() {
		return source;
	}

	public Strategy getStrategy() {
		return strategy;
	}

	public Layout getLayout() {
		return layout;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Project getProject() {
		return this.project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public static enum Source {
		GENOMIC_DNA, TOTAL_RNA, MRNA, GENOMIC_RNA, AMPLICON, METAGENOMIC
	}
}
