package ca.corefacility.bioinformatics.irida.model.workflow.description;

import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyToolDataTableException;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyToolDataService;
import com.github.jmchilton.blend4j.galaxy.ToolDataClient;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

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

    public List<String> getLabels() throws GalaxyToolDataTableException {
        List<String> labels = new ArrayList<>();
        labels.add("kmerdb_label_01");
        labels.add("kmerdb_label_02");
        return labels;
        // return galaxyToolDataService.getToolDataTable(this.getName()).getFieldsForColumn(galaxyToolDataTable.getDisplayColumn());
    }

    public List<String> getValues() throws GalaxyToolDataTableException {
        List<String> values = new ArrayList<>();
        values.add("kmerdb_value_01");
        values.add("kmerdb_value_02");
        return values;
        // return galaxyToolDataService.getToolDataTable(this.getName()).getFieldsForColumn(galaxyToolDataTable.getParameterColumn());
    }

    @Override
    public String toString() {
        return "IridaWorkflowDynamicSourceGalaxy [type=GalaxyToolDataTable name=" + this.name + "]";
    }
}
