package ca.corefacility.bioinformatics.irida.web.controller.test.unit.support;

import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.Resource;

/**
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class IdentifiableTestResource extends Resource<Identifier, IdentifiableTestEntity> {

    public IdentifiableTestResource() {
        super(new IdentifiableTestEntity());
    }

    public IdentifiableTestResource(IdentifiableTestEntity e) {
        super(e);
    }

    public String getNonNull() {
        return resource.getNonNull();
    }

    public void setNonNull(String nonNull) {
        this.resource.setNonNull(nonNull);
    }
}
