package ca.corefacility.bioinformatics.irida.config.conditions;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * A {@link Condition} for testing if the current platform is a Windows
 * platform.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
public class WindowsPlatformCondition implements Condition {

	@Override
	public boolean matches(ConditionContext context,
			AnnotatedTypeMetadata metadata) {
		return System.getProperty("os.name").toLowerCase().contains("win");
	}

}
