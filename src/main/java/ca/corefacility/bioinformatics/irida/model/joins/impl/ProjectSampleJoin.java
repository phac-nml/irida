package ca.corefacility.bioinformatics.irida.model.joins.impl;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Sample;
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
@Table(name="project_sample")
@Audited
public class ProjectSampleJoin implements Join<Project,Sample>{

    public ProjectSampleJoin(){
        createdDate = new Date();
    }
    
    public ProjectSampleJoin(Project subject, Sample object){
        this.project=subject;
        this.sample=object;
        createdDate = new Date();
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    
    @ManyToOne
    @JoinColumn(name="project_id")
    private Project project;
    
    @ManyToOne
    @JoinColumn(name="sample_id")
    private Sample sample;
    
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
    public Sample getObject() {
        return sample;
    }

    @Override
    public void setObject(Sample object) {
        this.sample=object;
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
