package ca.corefacility.bioinformatics.irida.model.joins;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.IridaThing;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Interface that the join classes should extend. Classes that extend this can
 * add additional fields that can be persisted in the database. Implementations
 * will have a "subject" and "object" that are the 2 fields being joined, then a
 * creation timestamp.
 *
 * @param <SubjectType> the type of the owning object
 * @param <ObjectType>  the type of the owned object
 */
public interface Join<SubjectType extends IridaThing, ObjectType extends IridaThing> extends IridaThing {
	/**
	 * Get the owning object in the relationship.
	 *
	 * @return the owning object of the relationship.
	 */
	public SubjectType getSubject();

	/**
	 * Get the owned object in the relationship.
	 *
	 * @return the owned object of the relationship.
	 */
	public ObjectType getObject();

	/**
	 * Get the timestamp for this object
	 *
	 * @return A {@link Date} object of the timestamp
	 */
	@Schema(implementation = Long.class, description = "Epoch time in milliseconds")
	public Date getTimestamp();

	/**
	 * By default, we will return the label of the subject of the join.
	 *
	 * @return The label of the object
	 */
	public default String getLabel() {
		return getSubject().getLabel();
	}
}
