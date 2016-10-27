package ca.corefacility.bioinformatics.irida.ria.web.components.linelist;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList;

/**
 * Storage class for get line list templates.
 */
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class LineListTemplates {

	private static final List<LineListField> DEFAULT_TEMPLATE = ImmutableList
			.of(new LineListField("identifier", "text"), new LineListField("label", "text"),
					new LineListField("PFGE-XbaI-pattern", "text"), new LineListField("PFGE-BlnI-pattern", "text"),
					new LineListField("NLEP #", "text"), new LineListField("SubmittedNumber", "text"),
					new LineListField("Province", "text"), new LineListField("SourceSite", "text"),
					new LineListField("SourceType", "text"), new LineListField("PatientAge", "text"),
					new LineListField("PatientSex", "text"), new LineListField("Genus", "text"),
					new LineListField("Serotype", "text"), new LineListField("ReceivedDate", "text"),
					new LineListField("UploadDate", "text"), new LineListField("IsolatDate", "text"),
					new LineListField("SourceCity", "text"), new LineListField("UploadModifiedDate", "text"),
					new LineListField("Comments", "text"), new LineListField("Outbreak", "text"),
					new LineListField("Phagetype", "text"), new LineListField("Traveled_To", "text"),
					new LineListField("Exposure", "text"));

	private static final List<LineListField> INTERESTING_TEMPLATE = ImmutableList
			.of(new LineListField("identifier", "text"), new LineListField("label", "text"),
					new LineListField("NLEP #", "text"), new LineListField("Province", "text"),
					new LineListField("SourceType", "text"), new LineListField("Genus", "text"),
					new LineListField("Serotype", "text"), new LineListField("uniqueField", "text"));

	private Map<String, List> templates;

	public LineListTemplates() {
		templates = new HashMap<>();

		templates.put("default", DEFAULT_TEMPLATE);
		templates.put("interesting", INTERESTING_TEMPLATE);
	}

	public void addTemplate(String name, List<LineListField> fields) {
		templates.put(name, fields);
	}

	public Set<String> getTemplateNames() {
		return templates.keySet();
	}

	public List<LineListField> getTemplate(String name) {
		if (templates.containsKey(name)) {
			return templates.get(name);
		} else {
			return ImmutableList.of();
		}
	}

	public boolean doesTemplateExists(String template) {
		return templates.containsKey(template);
	}
}
