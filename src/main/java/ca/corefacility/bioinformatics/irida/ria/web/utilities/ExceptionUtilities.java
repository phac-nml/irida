package ca.corefacility.bioinformatics.irida.ria.web.utilities;

import java.util.Locale;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.context.MessageSource;
import org.springframework.transaction.TransactionSystemException;

import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIConstraintViolationException;

/**
 * Utilities class for Exception handling.
 */

public class ExceptionUtilities {

	/**
	 * Method to unwrap a {@link TransactionSystemException} to create the error message from the root
	 * {@link ConstraintViolationException} cause and throw a UIConstraintViolationException. Required when using the
	 * hibernate validator in a {@literal @}Transactional as the root cause is wrapped in a
	 * {@link TransactionSystemException}
	 *
	 * @param exception     The exception to get the root cause from
	 * @param locale        Current users {@link Locale}
	 * @param messageSource the message source for i18n
	 * @throws UIConstraintViolationException if a project owners metadata role is set to anything other than the
	 *                                        highest level
	 */
	public static void throwConstraintViolationException(TransactionSystemException exception, Locale locale,
			MessageSource messageSource) throws UIConstraintViolationException {
		ConstraintViolationException constraintViolationException;
		String constraintViolationMessage = "";
		if (exception.getRootCause() instanceof ConstraintViolationException) {
			constraintViolationException = (ConstraintViolationException) exception.getRootCause();
			Set<ConstraintViolation<?>> constraintViolationSet = constraintViolationException.getConstraintViolations();
			for (ConstraintViolation constraintViolation : constraintViolationSet) {
				// The constraintViolation.getMessage() returns the i18n key in this case
				constraintViolationMessage +=
						messageSource.getMessage(constraintViolation.getMessage(), new Object[] {}, locale) + "\n";
			}
		}
		throw new UIConstraintViolationException(constraintViolationMessage);
	}
}
