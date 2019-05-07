package ca.corefacility.bioinformatics.irida.config.services.conditions;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

import net.matlux.NreplServerSpring;

/**
 * Condition for creating {@link NreplServerSpring} Spring Bean. If nrepl port not set or set to "-1", don't
 * create bean.
 */
public class NreplServerSpringCondition implements Condition {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		Environment env = context.getEnvironment();
		if (env == null)
			return false;
		String property = env.getProperty("irida.debug.nrepl.server.port", "-1");
		return !property.isEmpty() && !property.equals("-1");
	}
}
