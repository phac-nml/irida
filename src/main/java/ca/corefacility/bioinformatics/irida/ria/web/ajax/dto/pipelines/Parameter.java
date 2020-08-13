package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.pipelines;

public class Parameter {
		String label;
		String value;
		String name;

		public Parameter(String label, String value, String name) {
			this.label = label;
			this.value = value;
			this.name = name;
		}

		public String getLabel() {
			return label;
		}

		public String getValue() {
			return value;
		}

	public void setValue(String value) {
		this.value = value;
	}

	public String getName() {
			return name;
		}
}
