
package ca.corefacility.bioinformatics.irida.model;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.envers.Audited;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@Entity
@Table(name="miseq_run")
@Audited
public class MiseqRun implements IridaThing, Comparable<MiseqRun>{
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private String investigatorName;
    
    private String projectName;
    
    private String experimentName;
    
    private String workflow;
    
    private String application;
    
    private String assay;
    
	@Lob
    private String description;
    
    private String chemistry;
    
    private Boolean enabled = true;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDate;

    public MiseqRun(){
        createdDate = new Date();
        modifiedDate = createdDate;
    }
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInvestigatorName() {
        return investigatorName;
    }

    public void setInvestigatorName(String investigatorName) {
        this.investigatorName = investigatorName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getExperimentName() {
        return experimentName;
    }

    public void setExperimentName(String experimentName) {
        this.experimentName = experimentName;
    }

    public String getWorkflow() {
        return workflow;
    }

    public void setWorkflow(String workflow) {
        this.workflow = workflow;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getAssay() {
        return assay;
    }

    public void setAssay(String assay) {
        this.assay = assay;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getChemistry() {
        return chemistry;
    }

    public void setChemistry(String chemistry) {
        this.chemistry = chemistry;
    }
    
    @Override
    public int compareTo(MiseqRun p) {
        return modifiedDate.compareTo(p.modifiedDate);
    }

    @Override
    public String getLabel() {
        return projectName;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean valid) {
        this.enabled = valid;
    }

    @Override
    public Date getTimestamp() {
        return createdDate;
    }

    @Override
    public void setTimestamp(Date date) {
        this.createdDate = date;
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
    public int hashCode() {
        return Objects.hash(createdDate,modifiedDate,application,assay,chemistry,description,experimentName,investigatorName,projectName,workflow);
    }    

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MiseqRun other = (MiseqRun) obj;
        if (Objects.equals(this.investigatorName, other.investigatorName)
                && Objects.equals(this.projectName, other.projectName) 
                && Objects.equals(this.experimentName, other.experimentName)
                && Objects.equals(this.workflow, other.workflow)
                && Objects.equals(this.application, other.application)
                && Objects.equals(this.assay, other.assay)
                && Objects.equals(this.description, other.description)
                && Objects.equals(this.chemistry, other.chemistry)
                && Objects.equals(this.createdDate, other.createdDate)
                && Objects.equals(this.modifiedDate, other.modifiedDate)) {
            return true;
        }
        
        return false;
    }

    @Override
    public String toString() {
        return "MiseqRun{" + "id=" + id + ", investigatorName=" + investigatorName + ", projectName=" + projectName + ", description=" + description + ", createdDate=" + createdDate + '}';
    }

}
