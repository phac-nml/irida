package ca.corefacility.bioinformatics.irida.web.assembler.resource;

import ca.corefacility.bioinformatics.irida.model.roles.Auditable;
import ca.corefacility.bioinformatics.irida.model.roles.Identifiable;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Audit;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.hateoas.ResourceSupport;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Date;

/**
 * A generic container for all resources.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public abstract class Resource<IdentifierType extends Identifier,
        Type extends Identifiable<IdentifierType> & Auditable<Audit>> extends ResourceSupport {

    /**
     * the resource exposed by this container (not serialized).
     */
    @JsonIgnore
    protected Type resource;

    /**
     * Constructor for a resource container.
     *
     * @param resource the resource to be serialized.
     */
    public Resource(Type resource) {
        this.resource = resource;
    }

    /**
     * Get the concrete instance of the resource.
     *
     * @return the resource.
     */
    @JsonIgnore
    @XmlTransient // *REALLY* ignore this property, **PLEASE**.
    public Type getResource() {
        return resource;
    }

    /**
     * Set the resource to be serialized.
     *
     * @param resource the resource to be serialized.
     */
    public void setResource(Type resource) {
        this.resource = resource;
    }

    /**
     * All serialized objects should include the creation date of the resource.
     *
     * @return the creation date of the resource.
     */
    @XmlElement
    public Date getDateCreated() {
        return resource.getAuditInformation().getCreated();
    }

    /**
     * All serialized objects should also include their identifier.
     *
     * @return the identifier of the resource.
     */
    @XmlElement
    public String getIdentifier() {
        return resource.getIdentifier().getIdentifier();
    }
}
