package sme.perf.utility;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class LogHelperTest {


	@Test
	public void test() {
		try{
			LogHelper.debug("Hello, Debug!");
//			LogHelper.debug("Hello, Debug and {}", "Object");
//			LogHelper.debug("Hello, Debug, {} and {}", new Object[]{ "Object1", "Object2"});
			
			LogHelper.info("Hello, Info!");
//			LogHelper.info("Hello, Info and {}", "Object");
//			LogHelper.info("Hello, Info, {} and {}", new Object[]{ "Object1", "Object2"});
			
			LogHelper.error("Hello, Error!");
//			LogHelper.error("Hello, Error and {}", "Object");
//			LogHelper.error("Hello, Error, {} and {}", new Object[]{ "Object1", "Object2"});
//			
//			LogHelper.error("Exception is found!", new Exception("My Exception"));
			
			LogHelper.error(new Exception("My Exception"));
		}
		catch(Exception ex){
			fail(ex.getMessage());
		}
	}
}
