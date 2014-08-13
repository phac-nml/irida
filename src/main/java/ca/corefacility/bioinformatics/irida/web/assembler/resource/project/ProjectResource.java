package ca.corefacility.bioinformatics.irida.web.assembler.resource.project;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.IdentifiableResource;

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
	public String getProjectDescription() {
		return resource.getProjectDescription();
	}

	public void setProjectDescription(String projectDescription) {
		resource.setProjectDescription(projectDescription);
	}
}
