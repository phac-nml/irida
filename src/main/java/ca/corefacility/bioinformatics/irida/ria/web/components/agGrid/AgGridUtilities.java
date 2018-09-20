package ca.corefacility.bioinformatics.irida.ria.web.components.agGrid;

import java.util.UUID;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

public class AgGridUtilities {
	public static BidiMap<String, String> fields = new DualHashBidiMap<>();

	public static String formatFieldLabel(String field) {
		if (!fields.containsKey(field)) {
			fields.put(field, UUID.randomUUID()
					.toString());
		}
		return fields.get(field);
	}

	public static String getField(String label) {
		BidiMap<String, String> rMap = fields.inverseBidiMap();
		return rMap.getOrDefault(label, label);
	}
}
