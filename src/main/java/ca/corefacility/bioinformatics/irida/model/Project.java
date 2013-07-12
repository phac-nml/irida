package ca.corefacility.bioinformatics.irida.model;

import ca.corefacility.bioinformatics.irida.model.alibaba.IridaThing;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Audit;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import org.hibernate.envers.Audited;
import org.openrdf.annotations.Iri;

/**
 * A project object.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Entity
@Table(name="project")
@Audited
@Iri(Project.PREFIX + Project.TYPE)
public class Project implements IridaThing<Project,Audit,Identifier>, Comparable<Project> {
    public static final String PREFIX = "http://corefacility.ca/irida/";
    public static final String TYPE = "Project";
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Transient
    private Identifier identifier;
    @NotNull(message = "{project.name.notnull}")
    @Iri(PREFIX + "projectName")
    private String name;
    
    @NotNull
    @Transient
    private Audit audit;

    public Project() {
        audit = new Audit();
    }

    public Project(Identifier id) {
        this();
        this.identifier = id;
    }

    public Project(String name) {
        this();
        this.name = name;
    }

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
            return Objects.equals(identifier, p.identifier)
                    && Objects.equals(name, p.name);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Project p) {
        return audit.compareTo(p.audit);
    }

    @Override
    public Audit getAuditInformation() {
        return audit;
    }

    @Override
    public Identifier getIdentifier() {
        return identifier;
    }

    @Override
    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public void setAuditInformation(Audit audit) {
        this.audit = audit;
    }

    @Override
    public String getLabel() {
        return name;
    }

    @Override
    public Project copy() {
        Project p = new Project();
        p.setName(getName());
        return p;
    }
    
}
