package ca.corefacility.bioinformatics.irida.web.assembler.resource;

import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.Resource;

import javax.xml.bind.annotation.XmlElement;

/**
 * An implementation of a resource that has labels associated with it.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class LabelledRelationshipResource extends Resource<Identifier, Relationship> {
    @XmlElement
    private String label;

    public LabelledRelationshipResource(String label, Relationship r) {
        super(r);
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * We don't want to expose the {@link Identifier} for the {@link Relationship}, but rather the {@link Identifier}
     * for the object of the {@link Relationship}.
     *
     * @return the {@link Identifier} for the object of the {@link Relationship}.
     */
    @Override
    public String getIdentifier() {
        return resource.getObject().getIdentifier();
    }
}
