package ca.corefacility.bioinformatics.irida.model.workflow.description;

import javax.xml.bind.annotation.XmlElement;

public class IridaWorkflowDynamicSource {

    @XmlElement(name = "galaxyToolDataTable")
    private IridaWorkflowGalaxyToolDataTable galaxyToolDataTable;

    public IridaWorkflowGalaxyToolDataTable getGalaxyToolDataTable() {
        return galaxyToolDataTable;
    }

    @Override
    public String toString() {
        return "IridaWorkflowDynamicSource [type=GalaxyToolDataTable name=" + galaxyToolDataTable.getName() + "]";
    }
}
