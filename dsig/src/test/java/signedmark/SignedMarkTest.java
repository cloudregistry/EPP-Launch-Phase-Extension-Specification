package signedmark;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.*;


public class SignedMarkTest {

	@Test
	public void testToFromXML() throws Exception {
		// Create mark
		Mark mark = new Mark();
		mark.setName("Example One");
		List<String> labels = new ArrayList<String>();
		labels.add("example-one");
		labels.add("exampleone");
		mark.setLabels(labels);
		mark.setIssuer("IP Clearinghouse");
		mark.setNumber("GE 3933232");
		mark.setEntitlement("owner");
		mark.setRegDate(new Date());
		mark.setExDate(new Date());
		mark.setCountry("AU");
		mark.setRegion("VIC");
		SignedMark signedMark = new SignedMark();
		signedMark.setMark(mark);
		signedMark.setSerial("123456");
		signedMark.setExDate(new Date());
		
		// Create the XML with XMLSignature
		byte[] xml = signedMark.toXML();
		
		// Validate the XML with XMLSignature
		signedMark.fromXML(xml);	
	}

}

