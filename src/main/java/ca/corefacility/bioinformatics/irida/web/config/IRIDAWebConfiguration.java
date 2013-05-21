package ca.corefacility.bioinformatics.irida.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.hateoas.config.EnableEntityLinks;

/**
 * Spring HATEOAS does not provide XML configuration facilities, but we want to enable EntityLink support. Thus, this
 * class is used minimally to turn on EntityLink support and to load the other XML-based configuration that we already
 * have. Perhaps we will move to purely JavaConfig-based configuration in the future, but not yet.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@Configuration
@ImportResource(
        {"classpath:spring/api/applicationContext-api.xml",
                "classpath*:/ca/corefacility/bioinformatics/irida/config/applicationContext-api.xml"})
@EnableEntityLinks
public class IRIDAWebConfiguration {
}
