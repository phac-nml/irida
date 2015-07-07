package ca.corefacility.bioinformatics.irida.ria.unit.web;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyNoMoreInteractions;
import static org.powermock.api.mockito.PowerMockito.verifyZeroInteractions;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.DelegatingServletOutputStream;

import com.github.dandelion.core.Context;
import com.github.dandelion.core.asset.Asset;
import com.github.dandelion.core.storage.AssetStorage;
import com.github.dandelion.core.storage.StorageEntry;
import com.github.dandelion.core.util.AssetUtils;
import com.github.dandelion.core.web.DandelionServlet;
import com.github.dandelion.core.web.WebConstants;

import ca.corefacility.bioinformatics.irida.ria.web.AssetDependencyDandelionServlet;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AssetUtils.class)
public class AssetDependencyDandelionServletTest {
	private AssetDependencyDandelionServletStub servlet;
	private DandelionServlet delegate;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private Context context;
	private AssetStorage assetStorage;

	@Before
	public void setUp() {
		this.delegate = mock(DandelionServlet.class);
		this.servlet = new AssetDependencyDandelionServletStub(delegate);
		this.request = mock(HttpServletRequest.class, Mockito.RETURNS_DEEP_STUBS);
		this.response = mock(HttpServletResponse.class);
		this.context = mock(Context.class);
		this.assetStorage = mock(AssetStorage.class);
	}

	@Test
	public void testRequestJavascript() throws ServletException, IOException {
		when(request.getRequestURI()).thenReturn("something that ends with js.js");

		servlet.doGet(request, response);

		verify(delegate).service(request, response);
		verify(request).getRequestURI();
		verifyNoMoreInteractions(request);
		verifyNoMoreInteractions(response);
	}

	@Test
	public void testRequestCSS() throws ServletException, IOException {
		when(request.getRequestURI()).thenReturn("something that ends with css.css");

		servlet.doGet(request, response);

		verify(delegate).service(request, response);
		verify(request).getRequestURI();
		verifyNoMoreInteractions(request);
		verifyNoMoreInteractions(response);
	}

	@Test
	public void testRequestWOFF() throws ServletException, IOException, URISyntaxException {
		final String cacheKey = "cacheKey";
		final String assetLocation = "AssetLocation";
		final ByteArrayOutputStream streamCapture = new ByteArrayOutputStream();
		final Asset asset = new Asset();
		asset.setConfigLocation(assetLocation);

		mockStatic(AssetUtils.class);

		when(request.getRequestURI())
				.thenReturn(String.format("/%s/something that doesn't end with css or js.woff", cacheKey));
		when(request.getAttribute(WebConstants.DANDELION_CONTEXT_ATTRIBUTE)).thenReturn(context);
		when(request.getServletContext().getRealPath(assetLocation))
				.thenReturn(getClass().getResource("/files/dialects/icons/good-icons.html").toURI().getPath());
		BDDMockito.given(AssetUtils.extractCacheKeyFromRequest(request)).willReturn(cacheKey);
		when(context.getAssetStorage()).thenReturn(assetStorage);
		when(assetStorage.get(cacheKey)).thenReturn(new StorageEntry(asset, null));
		when(response.getOutputStream()).thenReturn(new DelegatingServletOutputStream(streamCapture));

		servlet.doGet(request, response);

		verifyZeroInteractions(delegate);
		assertEquals("The content of the outputstream should be the contents of the test file.", "This is a test file.",
				streamCapture.toString());
	}

	@Test(expected = FileNotFoundException.class)
	public void testRequestMissing() throws ServletException, IOException, URISyntaxException {
		final String cacheKey = "cacheKey";
		final String assetLocation = "AssetLocation";
		final ByteArrayOutputStream streamCapture = new ByteArrayOutputStream();
		final Asset asset = new Asset();
		asset.setConfigLocation(assetLocation);

		mockStatic(AssetUtils.class);

		when(request.getRequestURI()).thenReturn(String.format("/%s/something that doesn't exist!", cacheKey));
		when(request.getAttribute(WebConstants.DANDELION_CONTEXT_ATTRIBUTE)).thenReturn(context);
		when(request.getServletContext().getRealPath(assetLocation))
				.thenReturn(getClass().getResource("/files/dialects/icons/good-icons.html").toURI().getPath());
		BDDMockito.given(AssetUtils.extractCacheKeyFromRequest(request)).willReturn(cacheKey);
		when(context.getAssetStorage()).thenReturn(assetStorage);
		when(assetStorage.get(cacheKey)).thenReturn(new StorageEntry(asset, null));
		when(response.getOutputStream()).thenReturn(new DelegatingServletOutputStream(streamCapture));

		servlet.doGet(request, response);
	}

	/**
	 * An inner subclass so that we can call the protected doGet method on
	 * {@link AssetDependencyDandelionServlet}.
	 * 
	 */
	private static class AssetDependencyDandelionServletStub extends AssetDependencyDandelionServlet {

		public AssetDependencyDandelionServletStub(DandelionServlet dandelionServlet) {
			super(dandelionServlet);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void doGet(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
			super.doGet(request, response);
		}
	}
}
