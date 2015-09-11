package ca.corefacility.bioinformatics.irida.ria.web.errors;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.util.Locale;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.service.EmailController;

/**
 * An controller that handles all exceptions that might be thrown when a user is
 * navigating around in the web interface.
 * 
 */
@ControllerAdvice(basePackages = "ca.corefacility.bioinformatics.irida.ria.web")
public class ExceptionHandlerController {
	public static final String NOT_FOUND_PAGE = "errors/not_found";
	public static final String ACCESS_DENIED_PAGE = "errors/access_denied";
	private static final String OTHER_ERROR_PAGE = "errors/error";
	private static final String OAUTH_ERROR_PAGE = "errors/oauth_error";
	private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerController.class);

	@Value("${mail.server.email}")
	private String adminEmail;
	
	@Value("${irida.administrative.notifications.email}")
	private String notificationAdminEmail;
	
	private final EmailController emailController;

	private final MessageSource messageSource;

	@Autowired
	public ExceptionHandlerController(final EmailController emailController, final MessageSource messageSource) {
		this.emailController = emailController;
		this.messageSource = messageSource;
	}

	/**
	 * Handle an {@link EntityNotFoundException} and return an http 404
	 * 
	 * @param ex
	 *            The EntityNotFoundException caught
	 * @return the name of the not_found view
	 */
	@ExceptionHandler(EntityNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ModelAndView handleResourceNotFoundException(EntityNotFoundException ex) {
		logger.error(ex.getMessage(), ex);
		ModelAndView modelAndView = new ModelAndView(NOT_FOUND_PAGE);
		modelAndView.addObject("adminEmail", adminEmail);
		return modelAndView;
	}

	/**
	 * Handle an {@link AccessDeniedException} and return an Http 403
	 * 
	 * @param ex
	 *            The caught {@link AccessDeniedException}
	 * @return name of the access denied view
	 */
	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ModelAndView handleAccessDeniedException(AccessDeniedException ex) {
		logger.error(ex.getMessage(), ex);
		ModelAndView modelAndView = new ModelAndView(ACCESS_DENIED_PAGE);
		modelAndView.addObject("adminEmail", adminEmail);
		return modelAndView;
	}

	/**
	 * Catch an {@link OAuthProblemException} and return an http 500 error
	 * 
	 * @param ex
	 *            the caught {@link OAuthProblemException}
	 * @return A {@link ModelAndView} containing the name of the oauth error
	 *         view
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
	 * Catch all other exception types and display a generic error page and 500
	 * error
	 * 
	 * @param ex
	 *            The caught exception
	 * @return Name of the generic error page
	 */
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handleOtherExceptions(Exception ex) {
		logger.error(ex.getMessage(), ex);
		ModelAndView modelAndView = new ModelAndView(OTHER_ERROR_PAGE);
		modelAndView.addObject("adminEmail", adminEmail);
		return modelAndView;
	}

	/**
	 * Catch and handle {@link IOException}s. Render an error that's just a
	 * general storage exception.
	 * 
	 * @param e
	 *            the exception that was originally thrown.
	 * @param locale
	 *            the locale of the request.
	 * @return an error message for general storage exceptions.
	 */
	@ExceptionHandler(IOException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelMap handleIOException(final IOException e, final Locale locale) {
		logger.error("Error writing sequence file", e);
		final ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("error_message",
				messageSource.getMessage("project.samples.files.upload.error.storageexception", null, locale));
		return modelMap;
	}

	/**
	 * Catch and handle all {@link StorageExceptions}. Try your best to figure
	 * out the root cause of the exception.
	 * 
	 * @param e
	 *            the exception that was originally thrown.
	 * @param locale
	 *            the locale of the request.
	 * @return an error message (hopefully) more specific than a general storage
	 *         exception.
	 */
	@ExceptionHandler(StorageException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelMap handleStorageException(final StorageException e, final Locale locale) {
		final ModelMap modelMap = new ModelMap();
		
		if (e.getCause() instanceof FileAlreadyExistsException) {
			logger.error("General storage exception: File already exists (inconsistent back-end state)", e);
			modelMap.addAttribute("error_message", messageSource
					.getMessage("project.samples.files.upload.error.filealreadyexistsexception", null, locale));
		} else if (e.getCause() instanceof FileSystemException) {
			final FileSystemException fse = (FileSystemException) e.getCause();
			if (fse.getMessage().contains("No space left on device")) {
				logger.error("General storage exception: No space left on device.", e);
				modelMap.addAttribute("error_message", messageSource
						.getMessage("project.samples.files.upload.error.nospaceleftondevice", null, locale));
			} else {
				logger.error("General storage exception: Unexpected reason.", e);
				modelMap.addAttribute("error_message",
						messageSource.getMessage("project.samples.files.upload.error.storageexception", null, locale));
			}
		} else {
			logger.error("General storage exception: Unexpected reason.", e);
			modelMap.addAttribute("error_message",
					messageSource.getMessage("project.samples.files.upload.error.storageexception", null, locale));
		}
		
		emailController.sendFilesystemExceptionEmail(notificationAdminEmail, e);
		
		return modelMap;
	}
}
