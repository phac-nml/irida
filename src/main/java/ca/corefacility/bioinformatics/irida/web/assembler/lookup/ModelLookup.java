package ca.corefacility.bioinformatics.irida.web.assembler.lookup;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.run.MiseqRun;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.IdentifiableResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.project.ProjectResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.sample.SampleResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.sequencefile.SequenceFileResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.sequencingrun.MiseqRunResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.user.UserResource;

/**
 * Service to look up what the mapping between model objects and their
 * associated IdentifiableResource classes are. Can also be used to see what
 * properties are available to those web resources.
 * 
 * 
 */
public class ModelLookup {
	private static Map<Class<? extends Object>, Class<? extends IdentifiableResource<? extends IridaThing>>> classes = new HashMap<>();

	static {
		classes.put(Project.class, ProjectResource.class);
		classes.put(Sample.class, SampleResource.class);
		classes.put(User.class, UserResource.class);
		classes.put(SequenceFile.class, SequenceFileResource.class);
		classes.put(MiseqRun.class, MiseqRunResource.class);
	}

	/**
	 * Get the web resource class for a given model class
	 * 
	 * @param clazz
	 *            The class to get the resource class for
	 * @return The web resource class
	 */
	public static Class<? extends IdentifiableResource<? extends IridaThing>> getResourceClass(
			Class<? extends Object> clazz) {
		return classes.get(clazz);
	}

	/**
	 * Get a list of the available properties for a given model class. Note the
	 * properties will be pulled from the web resource class, not the model
	 * class itself
	 * 
	 * @param clazz
	 *            The model class to find properties for
	 * @return A List<String> of the available properties for the class itself.
	 */
	public static List<String> getProperties(Class<? extends Object> clazz) {
		Class<? extends IdentifiableResource<? extends IridaThing>> resource = classes.get(clazz);
		BeanInfo info = null;
		try {
			info = Introspector.getBeanInfo(resource, IdentifiableResource.class);
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
