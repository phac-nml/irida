package ca.corefacility.bioinformatics.irida.model.joins.impl;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.User;
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
 * A join table and class for users and projects.
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Entity
@Table(name="project_user")
@Audited
public class ProjectUserJoin implements Join<Project,User>{

    public ProjectUserJoin(){
        createdDate = new Date();
    }
    
    public ProjectUserJoin(Project subject, User object){
        this.project=subject;
        this.user=object;
        createdDate = new Date();
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
    
}
