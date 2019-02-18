package ca.corefacility.bioinformatics.irida.model.sequenceFile;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.IridaResourceSupport;
import ca.corefacility.bioinformatics.irida.model.MutableIridaThing;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteStatus;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteSynchronizable;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.sample.QCEntry;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AbstractAnalysisSubmission;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Objects that were obtained from some sequencing platform.
 */
@Entity
@Table(name = "sequencing_object")
@EntityListeners(AuditingEntityListener.class)
@Audited
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class SequencingObject extends IridaResourceSupport implements MutableIridaThing, RemoteSynchronizable {

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

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "sequencingObject")
	private SampleSequencingObjectJoin sample;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "automated_assembly", unique = true, nullable = true)
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	private AnalysisSubmission automatedAssembly;
	
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "sistr_typing", unique = true, nullable = true)
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	private AnalysisSubmission sistrTyping;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "remote_status")
	private RemoteStatus remoteStatus;

	@OneToMany(mappedBy = "sequencingObject", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	@NotAudited
	private Set<QCEntry> qcEntries;

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.DETACH, mappedBy = "inputFiles")
	private List<AnalysisSubmission> analysisSubmissions;

	@Enumerated(EnumType.STRING)
	@Column(name="processing_state")
	private ProcessingState processingState;

	@Column(name = "file_processor")
	private String fileProcessor;

	public SequencingObject() {
		createdDate = new Date();
		processingState = ProcessingState.UNPROCESSED;
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

	/**
	 * Get the {@link SequenceFile} with the given id in this object's files
	 * collection
	 * 
	 * @param id
	 *            the ID of the {@link SequenceFile} to get
	 * @return a {@link SequenceFile}
	 */
	public SequenceFile getFileWithId(Long id) {
		Set<SequenceFile> files = getFiles();

		return files.stream().filter(s -> s.getId().equals(id)).findAny()
				.orElseThrow(() -> new EntityNotFoundException("No file with id " + id + " in this SequencingObject"));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SequencingObject) {
			SequencingObject seqObj = (SequencingObject) obj;

			return Objects.equals(createdDate, seqObj.createdDate);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(createdDate);
	}

	@JsonIgnore
	public AnalysisSubmission getAutomatedAssembly() {
		return automatedAssembly;
	}

	public void setAutomatedAssembly(AnalysisSubmission automatedAssembly) {
		this.automatedAssembly = automatedAssembly;
	}
	
	@JsonIgnore
	public AnalysisSubmission getSistrTyping() {
		return sistrTyping;
	}

	public void setSistrTyping(AnalysisSubmission sistrTyping) {
		this.sistrTyping = sistrTyping;
	}

	@Override
	public RemoteStatus getRemoteStatus() {
		return remoteStatus;
	}

	@Override
	public void setRemoteStatus(RemoteStatus remoteStatus) {
		this.remoteStatus = remoteStatus;
	}

	@JsonIgnore
	public Set<QCEntry> getQcEntries() {
		return qcEntries;
	}
	
	@JsonIgnore
	public void setQcEntries(Set<QCEntry> qcEntries) {
		this.qcEntries = qcEntries;
	}

	public void setProcessingState(ProcessingState processingState){
		this.processingState = processingState;
	}

	public ProcessingState getProcessingState() {
		return processingState;
	}

	public void setFileProcessor(String fileProcessor) {
		this.fileProcessor = fileProcessor;
	}

	public String getFileProcessor() {
		return fileProcessor;
	}

	/**
	 * The status of the file processing upon upload
	 */
	public enum ProcessingState {
		// newly uploaded, no processing done
		UNPROCESSED,
		//picked up by file processor, waiting to process
		QUEUED,
		//currently processing
		PROCESSING,
		//done processing
		FINISHED,
		//error with file processing
		ERROR
	}
}
