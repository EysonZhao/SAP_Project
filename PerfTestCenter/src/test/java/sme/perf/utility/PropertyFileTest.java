package sme.perf.utility;

import static org.junit.Assert.*;

import org.junit.Test;

public class PropertyFileTest {

	@Test
	public void test() {
		assertTrue(PropertyFile.getValue("test").equals("testValue"));
	}

	@Test
	public void testStringFormat(){
		String target = "https://ubtjira.pvgl.sap.corp:8443/rest/api/2/issue/AWB-17433?fields=summary,status,priority,components,reporter,assignee,created";
		String formatter = "https://ubtjira.pvgl.sap.corp:8443/rest/api/2/issue/%s?fields=summary,status,priority,components,reporter,assignee,created";
		assertTrue(formatter.format(formatter, "AWB-17433").equals(target));
	}
}
