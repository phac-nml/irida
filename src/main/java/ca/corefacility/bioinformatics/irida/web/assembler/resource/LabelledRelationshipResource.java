package ca.corefacility.bioinformatics.irida.web.assembler.resource;

import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.Resource;

import javax.xml.bind.annotation.XmlElement;

/**
 * An implementation of a resource that only has a label and an identifier.
 * 
 */
public class LabelledRelationshipResource<Owner extends IridaThing, Child extends IridaThing> extends
		Resource<Join<Owner, Child>> {
	@XmlElement
	private String label;

	public LabelledRelationshipResource(String label, Join<Owner, Child> r) {
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
	 * We don't want to expose the id for the relationship, but rather the id
	 * for the object of the relationship.
	 * 
	 * @return the id for the object of the relationship.
	 */
	@Override
	public String getIdentifier() {
		return resource.getObject().getId().toString();
	}
}
