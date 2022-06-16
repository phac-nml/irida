package ca.corefacility.bioinformatics.irida.ria.web.errors;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.service.EmailController;

/**
 * Exception Handler for when a user is navigating around in the web interface.
 */
@ControllerAdvice(basePackages = "ca.corefacility.bioinformatics.irida.ria.web")
public class IridaCustomExceptionHandler extends ResponseEntityExceptionHandler {
	private static final String OAUTH_ERROR_PAGE = "errors/oauth_error";
	private static final Logger logger = LoggerFactory.getLogger(IridaCustomExceptionHandler.class);

	@Value("${mail.server.email}")
	private String adminEmail;

	@Value("${irida.administrative.notifications.email}")
	private String notificationAdminEmail;

	@Autowired
	private EmailController emailController;

	@Autowired
	private MessageSource messageSource;

	/**
	 * Handle an {@link EntityNotFoundException}
	 * 
	 * @param ex       The EntityNotFoundException caught
	 * @param response The HttpServletResponse
	 * @throws IOException If an input or output exception occurs
	 */
	@ExceptionHandler(EntityNotFoundException.class)
	public void handleResourceNotFoundException(EntityNotFoundException ex, HttpServletResponse response)
			throws IOException {
		logger.error(ex.getMessage(), ex);
		// Override the status code here and then let SpringBoot BasicErrorController handle the exception
		response.sendError(HttpStatus.NOT_FOUND.value());
	}

	/**
	 * Catch an {@link OAuthProblemException} and return an http 500 error
	 * 
	 * @param ex the caught {@link OAuthProblemException}
	 * @return A {@link ModelAndView} containing the name of the oauth error view
	 */
	@ExceptionHandler(OAuthProblemException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handleOAuthProblemException(OAuthProblemException ex) {
		logger.error("OAuth exception: " + ex.getMessage(), ex);

		ModelAndView modelAndView = new ModelAndView(OAUTH_ERROR_PAGE);
		modelAndView.addObject("exception", ex);
		modelAndView.addObject("adminEmail", adminEmail);
		return modelAndView;
	}

	/**
	 * Catch and handle {@link IOException}s. Render an error that's just a general storage exception.
	 * 
	 * @param e        the exception that was originally thrown.
	 * @param locale   the locale of the request.
	 * @param response The HttpServletResponse
	 * @throws IOException If an input or output exception occurs
	 */
	@ExceptionHandler(IOException.class)
	public void handleIOException(final IOException e, final Locale locale, HttpServletResponse response)
			throws IOException {
		logger.error("Error writing sequence file", e);
		response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
				messageSource.getMessage("project.samples.files.upload.error.storageexception", null, locale));
	}

	/**
	 * Catch and handle all {@link StorageException}s. Try your best to figure out the root cause of the exception.
	 * 
	 * @param e        the exception that was originally thrown.
	 * @param locale   the locale of the request.
	 * @param response The HttpServletResponse
	 * @throws IOException If an input or output exception occurs
	 */
	@ExceptionHandler(StorageException.class)
	public void handleStorageException(final StorageException e, final Locale locale, HttpServletResponse response)
			throws IOException {
		final ModelMap modelMap = new ModelMap();
		String errorMessage = "";

		if (e.getCause() instanceof FileAlreadyExistsException) {
			logger.error("General storage exception: File already exists (inconsistent back-end state)", e);
			errorMessage = messageSource.getMessage("project.samples.files.upload.error.filealreadyexistsexception",
					null, locale);
		} else if (e.getCause() instanceof FileSystemException) {
			final FileSystemException fse = (FileSystemException) e.getCause();
			if (fse.getMessage().contains("No space left on device")) {
				logger.error("General storage exception: No space left on device.", e);
				errorMessage = messageSource.getMessage("project.samples.files.upload.error.nospaceleftondevice", null,
						locale);
			} else {
				logger.error("General storage exception: Unexpected reason.", e);
				modelMap.addAttribute("error_message",
						messageSource.getMessage("project.samples.files.upload.error.storageexception", null, locale));
			}
		} else {
			logger.error("General storage exception: Unexpected reason.", e);
			errorMessage = messageSource.getMessage("project.samples.files.upload.error.storageexception", null,
					locale);
		}

		emailController.sendFilesystemExceptionEmail(notificationAdminEmail, e);

		response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorMessage);
	}
}
