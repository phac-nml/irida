package ca.corefacility.bioinformatics.irida.model.workflow.description;

import com.github.jmchilton.blend4j.galaxy.beans.TabularToolDataTable;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class IridaWorkflowDynamicSourceGalaxy implements IridaWorkflowDynamicSource {
    @XmlElement(name = "galaxyToolDataTable")
    private IridaWorkflowGalaxyToolDataTable galaxyToolDataTable;

    public IridaWorkflowDynamicSourceGalaxy() {
    }

    public IridaWorkflowGalaxyToolDataTable getSource() {
        return galaxyToolDataTable;
    }

    public String getName() {
        return galaxyToolDataTable.getName();
    }

    @Override
    public String toString() {
        return "IridaWorkflowDynamicSource [type=GalaxyToolDataTable name=" + galaxyToolDataTable.getName() + "]";
    }
}
