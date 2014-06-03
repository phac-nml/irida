package ca.corefacility.bioinformatics.irida.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.persistence.Entity;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.envers.tools.hbm2ddl.EnversSchemaGenerator;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * Export the current hibernate-generated schema (with auditing tables).
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
public class SchemaExporter {
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		Configuration configuration = new Configuration();

		Iterable<Class<?>> classes = getClasses("ca.corefacility.bioinformatics.irida.model");
		for (Class<?> c : classes) {
			if (AnnotationUtils.findAnnotation(c, Entity.class) != null) {
				configuration.addAnnotatedClass(c);
			}
		}
		configuration.setProperty(Environment.USER, "test").setProperty(Environment.PASS, "test")
				.setProperty(Environment.URL, "jdbc:mysql://localhost:3306/irida_test")
				.setProperty(Environment.DIALECT, "org.hibernate.dialect.MySQL5InnoDBDialect")
				.setProperty(Environment.DRIVER, "com.mysql.jdbc.Driver");

		new EnversSchemaGenerator(configuration).export().create(true, true);
	}

	/**
	 * Scans all classes accessible from the context class loader which belong
	 * to the given package and subpackages.
	 * 
	 * @param packageName
	 *            The base package
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private static Iterable<Class<?>> getClasses(String packageName) throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		List<Class<?>> classes = new ArrayList<>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}

		return classes;
	}

	/**
	 * Recursive method used to find all classes in a given directory and
	 * subdirs.
	 * 
	 * @param directory
	 *            The base directory
	 * @param packageName
	 *            The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
		List<Class<?>> classes = new ArrayList<>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				classes.addAll(findClasses(file, packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
			}
		}
		return classes;
	}
}
