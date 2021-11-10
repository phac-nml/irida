package ca.corefacility.bioinformatics.irida.ria.unit.web.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.utilities.SampleMetadataStorage;
import ca.corefacility.bioinformatics.irida.ria.utilities.SampleMetadataStorageRow;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIMetadataFileImportService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class UIMetadataFileImportServiceTest {
	private final Long PROJECT_ID = 1L;
	private final Project project = new Project();
	private final Sample sample = new Sample();
	private UIMetadataFileImportService service;
	private ProjectService projectService;
	private SampleService sampleService;

	@Before
	public void setUp() {
		this.projectService = Mockito.mock(ProjectService.class);
		this.sampleService = Mockito.mock(SampleService.class);
		service = new UIMetadataFileImportService(projectService, sampleService);
	}

	@Test
	public void parseCSV() {
		try {
			SampleMetadataStorage expected_storage = getSampleMetadataStorage();

			project.setId(PROJECT_ID);
			when(projectService.read(PROJECT_ID)).thenReturn(project);
			when(sampleService.getSampleBySampleName(project, "value2")).thenReturn(sample);

			MockMultipartFile file = new MockMultipartFile("file", "test.csv", MediaType.TEXT_PLAIN_VALUE,
					"header1,header2,header3\nvalue1,value2,value3".getBytes());

			byte[] byteArr = file.getBytes();
			InputStream inputStream = new ByteArrayInputStream(byteArr);
			SampleMetadataStorage actual_storage = service.parseCSV(PROJECT_ID, inputStream);
			assertEquals(actual_storage, expected_storage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private SampleMetadataStorage getSampleMetadataStorage() {
		SampleMetadataStorage expected_storage = new SampleMetadataStorage();
		List<String> headers_list = new ArrayList<>();
		headers_list.add("header1");
		headers_list.add("header2");
		headers_list.add("header3");
		expected_storage.setHeaders(headers_list);
		List<SampleMetadataStorageRow> rows = new ArrayList<>();
		Map<String, String> rowMap = new HashMap<>();
		rowMap.put("header1", "value1");
		rowMap.put("header2", "value2");
		rowMap.put("header3", "value3");
		rows.add(new SampleMetadataStorageRow(rowMap));
		expected_storage.setRows(rows);
		expected_storage.setSampleNameColumn("header2");
		return expected_storage;
	}
}
