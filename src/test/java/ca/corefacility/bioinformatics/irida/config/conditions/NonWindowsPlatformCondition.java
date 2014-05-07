package ca.corefacility.bioinformatics.irida.config.conditions;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * A {@link Condition} for testing if the current platform is a non-Windows
 * platform.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
public class NonWindowsPlatformCondition implements Condition {

	/**
	 * {@inheritDoc}
	 */
	public boolean matches(ConditionContext context,
			AnnotatedTypeMetadata metadata) {
		return !WindowsPlatformCondition.isWindows();
	}

}
