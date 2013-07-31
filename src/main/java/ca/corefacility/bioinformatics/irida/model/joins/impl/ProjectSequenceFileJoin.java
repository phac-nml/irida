package ca.corefacility.bioinformatics.irida.model.joins.impl;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
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

/**
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Entity
@Table(name = "sequencefile_project")
@Audited
public class ProjectSequenceFileJoin implements Join<Project, SequenceFile> {

	public ProjectSequenceFileJoin() {
		createdDate = new Date();
	}

	public ProjectSequenceFileJoin(Project subject, SequenceFile object) {
		this.sequenceFile = object;
		this.project = subject;
		createdDate = new Date();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;

	@ManyToOne
	@JoinColumn(name = "sequencefile_id")
	private SequenceFile sequenceFile;

	@ManyToOne
	@JoinColumn(name = "project_id")
	private Project project;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@Override
	public Project getSubject() {
		return project;
	}

	@Override
	public void setSubject(Project subject) {
		this.project = subject;
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
