
package ca.corefacility.bioinformatics.irida.model;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import org.hibernate.envers.Audited;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Entity
@Table(name="miseqRun")
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

}
