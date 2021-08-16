package org.egov.ifixadaptor;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class TransactionControllerTest extends IfixadaptorApplicationTests {

	@Override
	@Before
	public void setUp() {
		super.setUp();
	}

	@Test
	   public void testValidateBasicForFailure() throws Exception {
		
		String content=readFile(new File("/home/mani/wokspaces/msws/ifixadaptor/src/test/java/org/egov/ifixadaptor/testjson/basicFailure.json"));
	      String uri = "/event/v1/_push";
	      MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).content(content)
	         .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
	      
	      int status = mvcResult.getResponse().getStatus();
	      assertEquals(400, status);
	      //String response = mvcResult.getResponse().getContentAsString();
	      //System.out.println(response);
	     // assertEquals("id - size must be between 12 and 128", response);
	      
	   }
	
	
	@Test
	   public void testValidateBasicForSuccess() throws Exception {
		
		String content=readFile(new File("/home/mani/wokspaces/msws/ifixadaptor/src/test/java/org/egov/ifixadaptor/testjson/basicSuccess.json"));
	      String uri = "/event/v1/_push";
	      MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).content(content)
	         .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
	      
	      int status = mvcResult.getResponse().getStatus();
	      assertEquals(400, status);
	      //String response = mvcResult.getResponse().getContentAsString();
	      //System.out.println(response);
	     // assertEquals("id - size must be between 12 and 128", response);
	      
	   }
	public static String readFile(File srcFile) {
		String fileAsString=null;
		try {
			InputStream is = new FileInputStream(srcFile);
			BufferedReader buf = new BufferedReader(new InputStreamReader(is));
			String line = buf.readLine();
			StringBuilder sb = new StringBuilder();
			while (line != null) {
				sb.append(line).append("\n");
				line = buf.readLine();
			}
			is.close();
			fileAsString = sb.toString();
			//System.out.println("Contents : " + fileAsString);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileAsString;

	}

	 

}
