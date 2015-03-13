package ca.corefacility.bioinformatics.irida.web.assembler.lookup;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import ca.corefacility.bioinformatics.irida.model.IridaResourceSupport;

/**
 * Service to look up what the mapping between model objects and their
 * associated IdentifiableResource classes are. Can also be used to see what
 * properties are available to those web resources.
 * 
 * 
 */
public class ModelLookup {

	/**
	 * Get a list of the available properties for a given model class
	 * 
	 * @param clazz
	 *            The model class to find properties for
	 * @return A List<String> of the available properties for the class itself.
	 */
	public static List<String> getProperties(Class<? extends Object> clazz) {
		BeanInfo info = null;
		try {
			info = Introspector.getBeanInfo(clazz, IridaResourceSupport.class);
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}

		PropertyDescriptor[] propertyDescriptors = info.getPropertyDescriptors();
		List<String> names = new ArrayList<>();
		for (PropertyDescriptor p : propertyDescriptors) {
			names.add(p.getName());
		}

		return names;
	}
}
