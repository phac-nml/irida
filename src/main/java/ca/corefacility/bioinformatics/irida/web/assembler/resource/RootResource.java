package ca.corefacility.bioinformatics.irida.web.assembler.resource;

import javax.xml.bind.annotation.XmlRootElement;
import org.springframework.hateoas.ResourceSupport;

/**
 * Resource for sending links back when a client requests the root resource.
 *
 */
@XmlRootElement(name = "root")
public class RootResource extends ResourceSupport {
}
