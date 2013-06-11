package ca.corefacility.bioinformatics.irida.web.assembler.resource.relationship;

import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.Resource;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A resource for serializing {@link Relationship} resources.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@XmlRootElement(name = "relationship")
public class RelationshipResource extends Resource<Identifier, Relationship> {

    public RelationshipResource() {
        super(new Relationship());
    }
}
