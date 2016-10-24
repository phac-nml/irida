package ca.corefacility.bioinformatics.irida.ria.web.components.linelist;

import java.util.List;

public class LineListTemplate {
	public List<LineListField> getFields() {
		return fields;
	}

	public void setFields(List<LineListField> fields) {
		this.fields = fields;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private List<LineListField> fields;
	private String name;
}
