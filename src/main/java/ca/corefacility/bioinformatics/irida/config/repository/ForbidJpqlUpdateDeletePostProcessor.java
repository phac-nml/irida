package ca.corefacility.bioinformatics.irida.config.repository;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationNotAllowedException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * A {@link BeanPostProcessor} that inspects the {@link Query} methods on a repository bean and dies when a query is
 * written in JPQL that tries to modify the database with an update or delete. This is to prevent us from writing
 * queries that will skip through the auditing layer that envers provides.
 */
public class ForbidJpqlUpdateDeletePostProcessor implements PriorityOrdered, BeanPostProcessor {

	private static final Logger logger = LoggerFactory.getLogger(ForbidJpqlUpdateDeletePostProcessor.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (beanName.endsWith("Repository") && bean instanceof EnversRevisionRepositoryFactoryBean) {
			logger.trace("Found a repository class... [" + beanName + "]");
			final EnversRevisionRepositoryFactoryBean<?, ?, ?, ?> factory = (EnversRevisionRepositoryFactoryBean<?, ?, ?, ?>) bean;

			final Iterable<Method> queryMethods = factory.getRepositoryInformation().getQueryMethods();
			for (final Method queryMethod : queryMethods) {
				if (queryMethod.isAnnotationPresent(Query.class) || queryMethod.isAnnotationPresent(Modifying.class)) {

					final Query q = queryMethod.getAnnotation(Query.class);
					final String queryValue = q.value().toLowerCase();

					if (queryValue.contains("update") || queryValue.contains("delete")) {
						throw new BeanCreationNotAllowedException(beanName,
								"Update and Delete are not allowed in JPQL because Envers doesn't audit those queries. Remove the update or delete from the method ["
										+ queryMethod.getName() + "]");
					}
				}
			}
		}
		return bean;
	}
}
