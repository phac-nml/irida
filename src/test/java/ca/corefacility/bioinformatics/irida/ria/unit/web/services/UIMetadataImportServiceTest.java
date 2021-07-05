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

import ca.corefacility.bioinformatics.irida.ria.utilities.SampleMetadataStorage;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIMetadataImportService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import static org.junit.Assert.assertEquals;

public class UIMetadataImportServiceTest {
	private UIMetadataImportService service;

	@Before
	public void setUp() {
		ProjectService projectService = Mockito.mock(ProjectService.class);
		SampleService sampleService = Mockito.mock(SampleService.class);
		service = new UIMetadataImportService(projectService, sampleService);
	}

	@Test
	public void parseCSV() {
		try {
			Long projectId = 1L;
			SampleMetadataStorage expected_storage = new SampleMetadataStorage();
			List<String> headers_list = new ArrayList<>();
			headers_list.add("header1");
			headers_list.add("header2");
			headers_list.add("header3");
			expected_storage.saveHeaders(headers_list);
			List<Map<String, String>> rows = new ArrayList<>();
			Map<String, String> rowMap = new HashMap<>();
			rowMap.put("header1", "value1");
			rowMap.put("header2", "value2");
			rowMap.put("header3", "value3");
			rows.add(rowMap);
			expected_storage.saveRows(rows);

			MockMultipartFile file = new MockMultipartFile("file", "test.csv", MediaType.TEXT_PLAIN_VALUE,
					"header1,header2,header3\nvalue1,value2,value3".getBytes());

			byte[] byteArr = file.getBytes();
			InputStream inputStream = new ByteArrayInputStream(byteArr);
			SampleMetadataStorage actual_storage = service.parseCSV(projectId, inputStream);
			assertEquals(actual_storage, expected_storage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
