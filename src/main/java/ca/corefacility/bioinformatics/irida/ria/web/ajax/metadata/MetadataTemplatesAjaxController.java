package ca.corefacility.bioinformatics.irida.ria.web.ajax.metadata;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.NewMetadataTemplateRequest;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ProjectMetadataTemplate;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIMetadataTemplateService;

/**
 * Ajax controller for project metedata templates.
 */
@RestController
@RequestMapping("/ajax/metadata-templates")
public class MetadataTemplatesAjaxController {
	private final UIMetadataTemplateService service;

	@Autowired
	public MetadataTemplatesAjaxController(UIMetadataTemplateService service) {
		this.service = service;
	}

	/**
	 * Get a list of metadata templates for a specific project
	 *
	 * @param projectId Identifier for the project to get templates for.
	 * @return List of metadata templates with associate details.
	 */
	@RequestMapping("")
	public ResponseEntity<List<ProjectMetadataTemplate>> getProjectMetadataTemplates(@RequestParam Long projectId) {
		return ResponseEntity.ok(service.getProjectMetadataTemplates(projectId));
	}

	/**
	 * Create a new Metadata Template within a project.
	 *
	 * @param request {@link NewMetadataTemplateRequest} with details about the template to create.
	 * @return The identifier for the newly created template.
	 */
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<Long> createNewMetadataTemplate(@RequestBody NewMetadataTemplateRequest request) {
		return ResponseEntity.ok(service.createNewMetadataTemplate(request));
	}

	/**
	 * Get details about a specific metadata template
	 *
	 * @param templateId Identifier for a metadata template
	 * @return Details about a metadata template wrapped in a {@link ResponseEntity}
	 */
	@RequestMapping(value = "/{templateId}", method = RequestMethod.GET)
	public ResponseEntity<MetadataTemplate> getMetadataTemplateDetails(@PathVariable Long templateId) {
		return ResponseEntity.ok(service.getMetadataTemplateDetails(templateId));
	}

	/**
	 * Update either the name or description on a metadata template
	 *
	 * @param templateId Identifier for a metadata template
	 * @param field      The field to update on the template
	 * @param value      The new value to assign to the field
	 * @param locale     The current users {@link Locale}
	 * @return A message to the user about the status of the update wrapped in a {@link ResponseEntity}
	 */
	@RequestMapping(value = "/{templateId}", method = RequestMethod.PUT)
	public ResponseEntity<String> updateTemplateAttribute(@PathVariable Long templateId, @RequestParam String field,
			@RequestParam String value, Locale locale) {
		try {
			return ResponseEntity.ok(service.updateTemplateAttribute(templateId, field, value, locale));
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(e.getMessage());
		} catch (ConstraintViolationException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(e.getMessage());
		}
	}

	/**
	 * Download an excel version of a metadata template.
	 *
	 * @param templateId Identifier for a metadata template
	 * @param response   {@link HttpServletResponse}
	 * @throws IOException thrown if there was an error writing the file to the response
	 */
	@RequestMapping("/{templateId}/download")
	public void downloadMetadataTemplate(@PathVariable Long templateId, HttpServletResponse response)
			throws IOException {
		List<MetadataTemplateField> fields = service.getMetadataFieldsOnTemplate(templateId);
		List<String> headers = fields.stream()
				.map(MetadataTemplateField::getLabel)
				.collect(Collectors.toList());
		//Blank workbook
		XSSFWorkbook workbook = new XSSFWorkbook();

		//Create a blank sheet
		XSSFSheet worksheet = workbook.createSheet("fields");

		// Write the headers
		XSSFRow headerRow = worksheet.createRow(0);
		for (int i = 0; i < headers.size(); i++) {
			XSSFCell cell = headerRow.createCell(i);
			cell.setCellValue(headers.get(i));
		}

		response.setHeader("Content-Disposition", "attachment; filename=\"template.xlsx\"");
		ServletOutputStream stream = response.getOutputStream();
		workbook.write(stream);
		stream.flush();
	}
}
