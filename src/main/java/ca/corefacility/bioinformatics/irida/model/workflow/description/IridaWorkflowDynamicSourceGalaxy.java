package ca.corefacility.bioinformatics.irida.model.workflow.description;

import javax.xml.bind.annotation.XmlAttribute;
import java.util.List;
import java.util.Objects;

/**
 * Class providing access to information about a Dynamic Parameter Source that is supplied by Galaxy.
 * This class is intended to interface with Galaxy Tool Data Tables.
 *
 */
public class IridaWorkflowDynamicSourceGalaxy {
    @XmlAttribute
    private String name;

    @XmlAttribute
    private String displayColumn;

    @XmlAttribute
    private String parameterColumn;

    public IridaWorkflowDynamicSourceGalaxy() {
    }

    public IridaWorkflowDynamicSourceGalaxy(String name, String displayColumn, String parameterColumn) {
        this.name = name;
        this.displayColumn = displayColumn;
        this.parameterColumn = parameterColumn;
    }

    public String getName() {
        return name;
    }

    public String getDisplayColumn() {
        return displayColumn;
    }

    public String getParameterColumn() {
        return parameterColumn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, displayColumn, parameterColumn);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        else if (obj instanceof IridaWorkflowDynamicSourceGalaxy) {
            IridaWorkflowDynamicSourceGalaxy other = (IridaWorkflowDynamicSourceGalaxy) obj;

            return Objects.equals(name, other.name) &&
                    Objects.equals(displayColumn, other.displayColumn) &&
                    Objects.equals(parameterColumn, other.parameterColumn);
        }
        return false;
    }

    @Override
    public String toString() {
        return "IridaWorkflowDynamicSourceGalaxy [type=GalaxyToolDataTable name=" + this.name +
                ", displayColumn=" + this.displayColumn + ", parameterColumn=" + this.parameterColumn + "]";
    }
}
