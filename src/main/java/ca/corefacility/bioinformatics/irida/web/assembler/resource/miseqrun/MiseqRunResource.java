
package ca.corefacility.bioinformatics.irida.web.assembler.resource.miseqrun;

import ca.corefacility.bioinformatics.irida.model.MiseqRun;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.IdentifiableResource;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@XmlRootElement(name = "miseqRun")
public class MiseqRunResource extends IdentifiableResource<MiseqRun> {

    public MiseqRunResource() {
        super(new MiseqRun());
    }

    public MiseqRunResource(MiseqRun miseqRun) {
        super(miseqRun);
    }
    
    @XmlElement
    public String getInvestigatorName() {
        return resource.getInvestigatorName();
    }

    public void setInvestigatorName(String investigatorName) {
        resource.setInvestigatorName(investigatorName);
    }

    @XmlElement
    public String getProjectName() {
        return resource.getProjectName();
    }

    public void setProjectName(String projectName) {
        resource.setProjectName(projectName);
    }

    @XmlElement
    public String getExperimentName() {
        return resource.getExperimentName();
    }

    public void setExperimentName(String experimentName) {
        resource.setExperimentName(experimentName);
    }

    @XmlElement
    public String getWorkflow() {
        return resource.getWorkflow();
    }

    public void setWorkflow(String workflow) {
        resource.setWorkflow(workflow);
    }

    @XmlElement
    public String getApplication() {
        return resource.getApplication();
    }

    public void setApplication(String application) {
        resource.setApplication(application);
    }

    @XmlElement
    public String getAssay() {
        return resource.getAssay();
    }

    public void setAssay(String assay) {
        this.setAssay(assay);
    }

    @XmlElement
    public String getDescription() {
        return resource.getDescription();
    }

    public void setDescription(String description) {
        resource.setDescription(description);
    }

    @XmlElement
    public String getChemistry() {
        return resource.getChemistry();
    }

    public void setChemistry(String chemistry) {
        resource.setChemistry(chemistry);
    }    
    
}
