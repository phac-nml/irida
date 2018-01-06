package ca.corefacility.bioinformatics.irida.model.workflow.description;


import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * A parameter for a particular tool in a workflow.
 *
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
public class IridaWorkflowGalaxyToolDataTable {

    @XmlAttribute
    private String name;

    @XmlAttribute
    private String displayColumn;

    @XmlAttribute
    private String parameterColumn;

    public IridaWorkflowGalaxyToolDataTable() {
    }

    public IridaWorkflowGalaxyToolDataTable(String name, String displayColumn, String parameterColumn) {
        this.name = name;
        this.displayColumn = displayColumn;
        this.parameterColumn = parameterColumn;
    }

    /**
     * The name of the Galaxy Tool Data Table.
     *
     * @return The name of the Galaxy Tool Data Table.
     */
    public String getName() {
        return name;
    }

    /**
     * The Tool Data Table Column to display in the IRIDA Web UI.
     *
     * @return The Tool Data Table Column to display in the IRIDA Web UI.
     */
    public String getDisplayColumn() {
            return displayColumn;
        }

    /**
     * The Tool Data Table Column to use as a parameter value.
     *
     * @return The Tool Data Table Column use as a parameter value.
     */
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
        else if (obj instanceof IridaWorkflowGalaxyToolDataTable) {
            IridaWorkflowGalaxyToolDataTable other = (IridaWorkflowGalaxyToolDataTable) obj;

            return Objects.equals(name, other.name) &&
                    Objects.equals(displayColumn, other.displayColumn) &&
                    Objects.equals(parameterColumn, other.parameterColumn);
        }
        return false;
    }

    @Override public String toString() {
            return "IridaWorkflowGalaxyToolDataTable [name=" + name + ", " +
                    "displayColumn=" + displayColumn + ", " +
                    "parameterColumn=" + parameterColumn + "]";
    }
}

