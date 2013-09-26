package ca.corefacility.bioinformatics.irida.model.joins.impl;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;

/**
 * A join table and class for users and projects.
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Entity
@Table(name="project_user")
@Audited
public class ProjectUserJoin implements Join<Project,User>{

    public ProjectUserJoin(){
        createdDate = new Date();
		projectRole = ProjectRole.PROJECT_USER;
    }
    
    public ProjectUserJoin(Project subject, User object){
		this();
        this.project=subject;
        this.user=object;
    }
	
	public ProjectUserJoin(Project subject, User object, ProjectRole projectRole){
		this(subject,object);
		this.projectRole = projectRole;
	}
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    
    @ManyToOne
    @JoinColumn(name="project_id")
    private Project project;
    
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	private ProjectRole projectRole;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    
    @Override
    public Project getSubject() {
        return project;
    }

    @Override
    public void setSubject(Project subject) {
        this.project=subject;
    }

    @Override
    public User getObject() {
        return user;
    }

    @Override
    public void setObject(User object) {
        this.user=object;
    }

    @Override
    public Date getTimestamp() {
        return createdDate;
    }

    @Override
    public void setTimestamp(Date timestamp) {
        this.createdDate=timestamp;
    }

	/**
	 * Get the user's role on the project
	 * @return A representation of the user's project role
	 */
	public ProjectRole getProjectRole() {
		return projectRole;
	}

	/**
	 * Set the user's role on the project
	 * @param userRole The representation of the user's role on the project
	 */
	public void setProjectRole(ProjectRole userRole) {
		this.projectRole = userRole;
	}
    
}
