package signedmark;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/*******************************************************************************
 * The information in this document is proprietary to VeriSign and the VeriSign
 * Registry Business. It may not be used, reproduced, or disclosed without the
 * written approval of the General Manager of VeriSign Information Services.
 * 
 * PRIVILEGED AND CONFIDENTIAL VERISIGN PROPRIETARY INFORMATION (REGISTRY
 * SENSITIVE INFORMATION)
 * Copyright (c) 2006 VeriSign, Inc. All rights reserved.
 * **********************************************************
 */

// jgould -- Nov 21, 2012

public class XMLUtil {

	
	/** Format used for the XML Schema timeInstant data type with milliseconds. */
	public static final String TIME_INSTANT_FORMAT = "yyyy-MM-dd'T'HH':'mm':'ss'.'SSSS'Z'";


	public static void encodeString(Document doc, Element parent, String str,
			String namespaceURI, String qualifiedName) {
		if (str != null) {
			Element elm = doc.createElementNS(namespaceURI, qualifiedName);
			Text strVal = doc.createTextNode(str);
			elm.appendChild(strVal);
			parent.appendChild(elm);
		}
	}

	public static String encodeTimeInstant(Date aDate) {
		TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
		SimpleDateFormat formatter = new SimpleDateFormat(TIME_INSTANT_FORMAT);
		formatter.setTimeZone(utcTimeZone);

		return formatter.format(aDate);
	}

	public static void encodeTimeInstant(Document doc, Element parent,
			Date date, String namespaceURI, String qualifiedName) {
		if (date != null) {
			encodeString(doc, parent, encodeTimeInstant(date), namespaceURI,
					qualifiedName);
		}
	}
	
	public static void encodeStringList(Document doc, Element parent, List<String> list,
			String namespaceURI, String qualifiedName) {
		Element currElm;
		Text currVal;

		if (list != null) {
			for (String item : list) {

				currElm = doc.createElementNS(namespaceURI, qualifiedName);
				currVal = doc.createTextNode(item);

				currElm.appendChild(currVal);
				parent.appendChild(currElm);
			}
		}
	} 
	
	public static void marshalToStream(Document doc, OutputStream ostream, boolean indent) throws Exception {
		TransformerFactory transFac = TransformerFactory.newInstance();
		Transformer trans = transFac.newTransformer();
		if (indent) {
			trans.setOutputProperty(OutputKeys.INDENT, "yes");
			trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		}
		trans.setOutputProperty(OutputKeys.STANDALONE, "no");
		
		trans.transform(new DOMSource(doc), new StreamResult(ostream));
	}

	public static void marshalToStream(Element elm, OutputStream ostream, boolean indent) throws Exception {
		TransformerFactory transFac = TransformerFactory.newInstance();
		Transformer trans = transFac.newTransformer();
		if (indent) {
			trans.setOutputProperty(OutputKeys.INDENT, "yes");
			trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		}
		trans.setOutputProperty(OutputKeys.STANDALONE, "no");
		
		trans.transform(new DOMSource(elm), new StreamResult(ostream));
	}
	
}
