package ca.corefacility.bioinformatics.irida.web.spring;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

/**
 * A subclass of {@link MappingJackson2JsonView} that exposes a configuration option for the object mapper to use the
 * root name of the object as a key.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class MappingJackson2JsonViewResource extends MappingJackson2JsonView {

    private static final Logger logger = LoggerFactory.getLogger(MappingJackson2JsonViewResource.class);
    private Boolean wrapRootValue = Boolean.FALSE;

    /**
     * Whether to wrap the root value of a JSON encoded object. This is a shortcut for setting up an {@code
     * ObjectMapper} as follows:
     * <pre>
     * ObjectMapper mapper = new ObjectMapper();
     * mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
     * </pre>
     * <p>The default value is {@code false}.
     *
     * @param wrapRootValue whether or not to wrap the root value of the JSON object.
     */
    public void setWrapRootValue(Boolean wrapRootValue) {
        if (wrapRootValue) {
            logger.debug("Enabling wrap root value.");
        }
        this.wrapRootValue = wrapRootValue;
        configureWrapRootValue(wrapRootValue);
    }

    private void configureWrapRootValue(Boolean wrapRootValue) {
        if (wrapRootValue != null) {
            this.getObjectMapper().configure(SerializationFeature.WRAP_ROOT_VALUE, wrapRootValue);
        }
    }
}
