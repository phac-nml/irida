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
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;


/**
 * A project object.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Entity
@Table(name="project")
@Audited
public class Project implements IridaThing, Comparable<Project> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull(message = "{project.name.notnull}")
    private String name;
    
    private Boolean enabled = true;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDate;
	
	@Temporal(TemporalType.DATE)
	private Date anticipatedStartDate;
	
	@Lob
	private String projectDescription;
	
	@Lob
	private String projectDesign;
	
	@Lob
	private String dataAnalysisRequirements;
	
	@Lob
	private String additionalCoreWork;
	
	@Lob
	private String supplyRequirements;
	
	@Lob
	private String additionalCosts;

    public Project() {
        createdDate = new Date();
        modifiedDate = createdDate;
    }

    /**
     * Create a new {@link Project} with the given name
     * @param name The name of the project
     */
    public Project(String name) {
        this();
        this.name = name;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    

    @Override
    public boolean equals(Object other) {
        if (other instanceof Project) {
            Project p = (Project) other;
            return Objects.equals(createdDate, p.createdDate)
                    && Objects.equals(modifiedDate, p.modifiedDate)
                    && Objects.equals(name, p.name);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(createdDate,modifiedDate,name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Project p) {
        return modifiedDate.compareTo(p.modifiedDate);
    }

    @Override
    public String getLabel() {
        return name;
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

	public Date getAnticipatedStartDate() {
		return anticipatedStartDate;
	}

	public void setAnticipatedStartDate(Date anticipatedStartDate) {
		this.anticipatedStartDate = anticipatedStartDate;
	}

	public String getProjectDescription() {
		return projectDescription;
	}

	public void setProjectDescription(String projectDescription) {
		this.projectDescription = projectDescription;
	}

	public String getProjectDesign() {
		return projectDesign;
	}

	public void setProjectDesign(String projectDesign) {
		this.projectDesign = projectDesign;
	}

	public String getDataAnalysisRequirements() {
		return dataAnalysisRequirements;
	}

	public void setDataAnalysisRequirements(String dataAnalysisRequirements) {
		this.dataAnalysisRequirements = dataAnalysisRequirements;
	}

	public String getAdditionalCoreWork() {
		return additionalCoreWork;
	}

	public void setAdditionalCoreWork(String additionalCoreWork) {
		this.additionalCoreWork = additionalCoreWork;
	}

	public String getSupplyRequirements() {
		return supplyRequirements;
	}

	public void setSupplyRequirements(String supplyRequirements) {
		this.supplyRequirements = supplyRequirements;
	}

	public String getAdditionalCosts() {
		return additionalCosts;
	}

	public void setAdditionalCosts(String additionalCosts) {
		this.additionalCosts = additionalCosts;
	}
    
}
