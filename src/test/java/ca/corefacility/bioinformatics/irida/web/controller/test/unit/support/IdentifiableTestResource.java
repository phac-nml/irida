package ca.corefacility.bioinformatics.irida.web.controller.test.unit.support;

import ca.corefacility.bioinformatics.irida.web.assembler.resource.Resource;

/**
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class IdentifiableTestResource extends Resource<IdentifiableTestEntity> {

    public IdentifiableTestResource() {
        super(new IdentifiableTestEntity());
    }

    public IdentifiableTestResource(IdentifiableTestEntity e) {
        super(e);
    }

	@Override
	public String getIdentifier() {
		return null;
	}
}
