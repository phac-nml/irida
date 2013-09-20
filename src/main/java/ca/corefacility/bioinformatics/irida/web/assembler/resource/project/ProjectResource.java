package ca.corefacility.bioinformatics.irida.web.assembler.resource.project;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.IdentifiableResource;
import java.util.Date;

/**
 * A resource for {@link Project}s.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@XmlRootElement(name = "project")
public class ProjectResource extends IdentifiableResource<Project> {

    public ProjectResource() {
        super(new Project());
    }

    public ProjectResource(Project project) {
        super(project);
    }

    @XmlElement
    public String getName() {
        return resource.getName();
    }

    public void setName(String name) {
        this.resource.setName(name);
    }
	
	@XmlElement
	public Date getAnticipatedStartDate() {
		return resource.getAnticipatedStartDate();
	}

	public void setAnticipatedStartDate(Date anticipatedStartDate) {
		resource.setAnticipatedStartDate(anticipatedStartDate);
	}

	@XmlElement
	public String getProjectDescription() {
		return resource.getProjectDescription();
	}

	public void setProjectDescription(String projectDescription) {
		resource.setProjectDescription(projectDescription);
	}

	@XmlElement
	public String getProjectDesign() {
		return resource.getProjectDesign();
	}

	public void setProjectDesign(String projectDesign) {
		resource.setProjectDesign(projectDesign);
	}

	@XmlElement
	public String getDataAnalysisRequirements() {
		return resource.getDataAnalysisRequirements();
	}

	public void setDataAnalysisRequirements(String dataAnalysisRequirements) {
		resource.setDataAnalysisRequirements(dataAnalysisRequirements);
	}

	@XmlElement
	public String getAdditionalCoreWork() {
		return resource.getAdditionalCoreWork();
	}

	public void setAdditionalCoreWork(String additionalCoreWork) {
		resource.setAdditionalCoreWork(additionalCoreWork);
	}

	@XmlElement
	public String getSupplyRequirements() {
		return resource.getSupplyRequirements();
	}

	public void setSupplyRequirements(String supplyRequirements) {
		resource.setSupplyRequirements(supplyRequirements);
	}

	@XmlElement
	public String getAdditionalCosts() {
		return resource.getAdditionalCosts();
	}

	public void setAdditionalCosts(String additionalCosts) {
		resource.setAdditionalCosts(additionalCosts);
	}
}
