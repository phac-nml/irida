package ca.corefacility.bioinformatics.irida.config.conditions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Arrays;

/**
 * Checks to see if we're being run with a remote instance of Galaxy in mind.
 */
public class RemoteGalaxyCondition implements Condition {
    @Autowired
    private Environment environment;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return Arrays.stream(new String[]{ "remote.galaxy.test.url", "remote.galaxy.test.apikey" })
                .allMatch(p -> environment.containsProperty(p));
    }
}
