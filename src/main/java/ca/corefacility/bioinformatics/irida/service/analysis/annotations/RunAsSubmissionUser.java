package ca.corefacility.bioinformatics.irida.service.analysis.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * Annotation saying the function should be run as the user that submitted the
 * {@link AnalysisSubmission}
 * 
 * @see RunAsSubmissionUserAspect
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RunAsSubmissionUser {
	String value();
}
