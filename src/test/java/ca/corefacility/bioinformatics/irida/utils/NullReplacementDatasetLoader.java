package ca.corefacility.bioinformatics.irida.utils;

import com.github.springtestdbunit.dataset.AbstractDataSetLoader;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.springframework.core.io.Resource;

import java.io.InputStream;

/**
 * Spring DBUnit loader which replaces '[null]' with an actual NULL value in the database.
 */
public class NullReplacementDatasetLoader extends AbstractDataSetLoader {

	@Override
	protected IDataSet createDataSet(Resource resource) throws Exception {
		FlatXmlDataSetBuilder dataSetBuilder = new FlatXmlDataSetBuilder();
		dataSetBuilder.setColumnSensing(true);
		try (InputStream inputStream = resource.getInputStream()) {
			FlatXmlDataSet dataSet = dataSetBuilder.build(inputStream);
			ReplacementDataSet replacementSet = new ReplacementDataSet(dataSet);
			//Replace [null] with null values in the data
			replacementSet.addReplacementObject("[null]", null);

			return replacementSet;
		}
	}
}
