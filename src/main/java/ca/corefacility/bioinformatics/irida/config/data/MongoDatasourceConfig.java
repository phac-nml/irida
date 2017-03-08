package ca.corefacility.bioinformatics.irida.config.data;

import java.net.UnknownHostException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;

import ca.corefacility.bioinformatics.irida.model.sample.SampleMetadata;

import com.mongodb.MongoClient;

@Configuration
@Deprecated
public class MongoDatasourceConfig {

	private String mongoHost = "localhost";

	private String mongoDatabaseName = "test";

	@Bean
	public MongoTemplate mongoTemplate(MongoDbFactory mongoDbFactory) throws Exception {
		return new MongoTemplate(mongoDbFactory);
	}

	@Bean
	public MongoDbFactory mongoDbFactory(MongoClient mongo) throws UnknownHostException {
		return new SimpleMongoDbFactory(mongo, mongoDatabaseName);
	}

	@Bean
	public MongoClient mongoClient() throws UnknownHostException {
		return new MongoClient(mongoHost);
	}

	/**
	 * Clears the metadata store and loads example data into the mongodb store
	 * for development purposes.
	 * 
	 * @param mongoTemplate
	 *            {@link MongoTemplate} being used to clear the existing data
	 * @return {@link Jackson2RepositoryPopulatorFactoryBean}
	 */
	@Bean
	@Profile("dev")
	public Jackson2RepositoryPopulatorFactoryBean loadExampleData(MongoTemplate mongoTemplate) {

		// clearing the existing samples
		mongoTemplate.remove(new Query(), SampleMetadata.class);

		Jackson2RepositoryPopulatorFactoryBean jacksonRepositoryPopulatorFactoryBean = new Jackson2RepositoryPopulatorFactoryBean();

		ClassPathResource fileSystemResource = new ClassPathResource(
				"ca/corefacility/bioinformatics/irida/metadata/metadata.json");

		jacksonRepositoryPopulatorFactoryBean.setResources(new Resource[] { fileSystemResource });

		return jacksonRepositoryPopulatorFactoryBean;
	}
}
