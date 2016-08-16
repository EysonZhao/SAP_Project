package sme.perf.utility;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class JsonHelperTest {

	@Test
	public void testSerialArrayList(){
		List<List<String>> arrayList = new ArrayList<List<String>>();
		for(int i=0 ; i<3 ; i++){
			List<String> strList = new ArrayList<String>();
			for(int j=0 ; j<5 ; j++){
				strList.add("th" + j*100);
			}
			arrayList.add(strList);
		}
		JsonHelper jsonHelper = new JsonHelper();
		String jsonString = jsonHelper.serializeObject(arrayList);
		List<List<String>> arrayList2 = (List<List<String>>)jsonHelper.deserialObject(jsonString, List.class);
		assertTrue(arrayList2.size() == 3);
		for(int i=0 ;i<arrayList2.size() ; i++){
			assertTrue(arrayList2.get(i).size() == 5);
		}
	}

}
