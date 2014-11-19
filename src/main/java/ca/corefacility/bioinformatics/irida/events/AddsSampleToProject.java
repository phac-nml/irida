package ca.corefacility.bioinformatics.irida.events;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;

/**
 * Annotation used for service methods which add a {@link Sample} to a
 * {@link Project}. These methods should return a {@link ProjectSampleJoin}.
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
@Target(ElementType.METHOD)
public @interface AddsSampleToProject {

}
