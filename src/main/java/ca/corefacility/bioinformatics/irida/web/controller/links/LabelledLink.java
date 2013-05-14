package ca.corefacility.bioinformatics.irida.web.controller.links;

import org.springframework.hateoas.Link;

import javax.xml.bind.annotation.XmlElement;

/**
 * An implementation of a link that has labels associated with it.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class LabelledLink extends Link {
    @XmlElement
    private String label;

    public LabelledLink(Link l) {
        super(l.getHref(), l.getRel());
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
