package ca.corefacility.bioinformatics.irida.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;

/**
 * Class storing a request to upload sequence data to NCBI.
 */
@Entity
@Table(name = "ncbi_export_submission")
@Audited
@EntityListeners(AuditingEntityListener.class)
public class NcbiExportSubmission implements IridaThing {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
	private Project project;

	@ManyToMany(fetch = FetchType.EAGER)
	@CollectionTable(name = "ncbi_export_submission_single_files")
	private List<SequenceFile> singleFiles;

	@ManyToMany(fetch = FetchType.EAGER)
	@CollectionTable(name = "ncbi_export_submission_pair_files")
	private List<SequenceFilePair> pairFiles;

	private Date createdDate;

	@LastModifiedDate
	private Date modifiedDate;

	public NcbiExportSubmission() {
		createdDate = new Date();
	}

	public NcbiExportSubmission(Project project, List<SequenceFile> singleFiles, List<SequenceFilePair> pairFiles) {
		this.project = project;
		this.singleFiles = singleFiles;
		this.pairFiles = pairFiles;
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
	public String getLabel() {
		return "NCBI Submission Project " + project.getLabel() + " " + createdDate;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public Project getProject() {
		return project;
	}

	public List<SequenceFilePair> getPairFiles() {
		return pairFiles;
	}

	public List<SequenceFile> getSingleFiles() {
		return singleFiles;
	}

}
