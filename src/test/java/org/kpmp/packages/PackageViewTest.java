package org.kpmp.packages;

import static org.junit.Assert.assertEquals;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PackageViewTest {

	private PackageView packageView;

	@Before
	public void setUp() throws Exception {
		packageView = new PackageView(new JSONObject());
	}

	@After
	public void tearDown() throws Exception {
		packageView = null;
	}

	@Test
	public void testSetIsDownloadable() {
		packageView.setIsDownloadable(true);
		assertEquals(true, packageView.isDownloadable());
	}

	@Test
	public void testSetPackageJSON() {
		JSONObject packageInfo = new JSONObject();
		packageView.setPackageInfo(packageInfo);

		assertEquals(packageInfo, packageView.getPackageInfo());
	}

}
