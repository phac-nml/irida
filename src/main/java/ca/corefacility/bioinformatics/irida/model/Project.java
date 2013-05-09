package ca.corefacility.bioinformatics.irida.model;

import ca.corefacility.bioinformatics.irida.model.alibaba.ProjectIF;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Audit;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import java.util.Objects;
import javax.validation.constraints.NotNull;

/**
 * A project object.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class Project implements ProjectIF, Comparable<Project> {

    private Identifier id;
    @NotNull(message = "{project.name.notnull}")
    private String name;
    @NotNull
    private Audit audit;

    public Project() {
        audit = new Audit();
    }

    public Project(Identifier id) {
        this();
        this.id = id;
    }

    public Project(String name) {
        this();
        this.name = name;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Project) {
            Project p = (Project) other;
            return Objects.equals(id, p.id)
                    && Objects.equals(name, p.name);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
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
        return id;
    }

    @Override
    public void setIdentifier(Identifier identifier) {
        this.id = identifier;
    }

    @Override
    public void setAuditInformation(Audit audit) {
        this.audit = audit;
    }

    @Override
    public String getLabel() {
        return name;
    }
    
}
