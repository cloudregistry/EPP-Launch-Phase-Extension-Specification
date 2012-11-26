package signedmark;

import java.util.Date;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Mark {
	
	public static final String NS_URI = "urn:ietf:params:xml:ns:mark-1.0";
	
	public static final String NS_PREFIX = "mark";

	private String name;

	private List<String> labels;

	private String issuer;

	private String number;

	private String entitlement;

	private Date regDate;

	private Date exDate;

	private String country;

	private String region;

	
	
	public String getName() {
		return this.name;
	}



	public void setName(String aName) {
		this.name = aName;
	}



	public List<String> getLabels() {
		return this.labels;
	}



	public void setLabels(List<String> aLabels) {
		this.labels = aLabels;
	}



	public String getIssuer() {
		return this.issuer;
	}



	public void setIssuer(String aIssuer) {
		this.issuer = aIssuer;
	}



	public String getNumber() {
		return this.number;
	}



	public void setNumber(String aNumber) {
		this.number = aNumber;
	}



	public String getEntitlement() {
		return this.entitlement;
	}



	public void setEntitlement(String aEntitlement) {
		this.entitlement = aEntitlement;
	}



	public Date getRegDate() {
		return this.regDate;
	}



	public void setRegDate(Date aRegDate) {
		this.regDate = aRegDate;
	}



	public Date getExDate() {
		return this.exDate;
	}



	public void setExDate(Date aExDate) {
		this.exDate = aExDate;
	}



	public String getCountry() {
		return this.country;
	}



	public void setCountry(String aCountry) {
		this.country = aCountry;
	}



	public String getRegion() {
		return this.region;
	}



	public void setRegion(String aRegion) {
		this.region = aRegion;
	}



	public Element encode(Document doc) {

		// <smd:signedMark> element
		Element root = doc.createElementNS(NS_URI, NS_PREFIX + ":mark");
		
		// <smd:name> element
		XMLUtil.encodeString(doc, root, this.name, NS_URI, NS_PREFIX
				+ ":name");
		
		// <smd:label> elements
		XMLUtil.encodeStringList(doc, root, this.labels, NS_URI, NS_PREFIX
				+ ":label");
		
		// <smd:issuer> element
		XMLUtil.encodeString(doc, root, this.issuer, NS_URI, NS_PREFIX
				+ ":issuer");
		
		// <smd:number> element
		XMLUtil.encodeString(doc, root, this.number, NS_URI, NS_PREFIX
				+ ":number");

		// <smd:entitlement> element
		XMLUtil.encodeString(doc, root, this.entitlement, NS_URI, NS_PREFIX
				+ ":number");
		
		// <smd:regDate> element
		XMLUtil.encodeTimeInstant(doc, root, this.regDate, NS_URI, NS_PREFIX
				+ ":regDate");
		
		// <smd:exDate> element
		XMLUtil.encodeTimeInstant(doc, root, this.exDate, NS_URI, NS_PREFIX
				+ ":exDate");
		
		// <smd:country> element
		XMLUtil.encodeString(doc, root, this.country, NS_URI, NS_PREFIX
				+ ":country");

		// <smd:region> element
		XMLUtil.encodeString(doc, root, this.region, NS_URI, NS_PREFIX
				+ ":region");
		
		return root;
	}

}
