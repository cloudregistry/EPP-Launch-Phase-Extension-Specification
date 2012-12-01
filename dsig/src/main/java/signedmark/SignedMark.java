package signedmark;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sun.org.apache.xerces.internal.dom.DocumentImpl;

public class SignedMark {

	public static final String NS_URI = "urn:ietf:params:xml:ns:signedMark-1.0";

	public static final String NS_PREFIX = "smd";

	private String serial;

	List<String> zones;

	private Date exDate;

	private Mark mark;

	private static PrivateKey privateKey;

	private static PublicKey publicKey;

	private static final String KEYSTORE_FILENAME = "signedMark.jks";

	private static final String KEYSTORE_PASSWORD = "changeit";

	private static final String KEYSTORE_KEY_ALIAS = "signedMark";
	
	private static final String KEYSTORE_CERT_ALIAS = "signedMarkCert";
	
	static {
		try {
			loadKeys(KEYSTORE_FILENAME, KEYSTORE_PASSWORD);
		}
		catch (Exception ex) {
			System.err.println("Error loading keys from keystore " + KEYSTORE_FILENAME);
			System.exit(1);
		}
	}

	private static void loadKeys(String keyStoreName, String password) throws Exception {
		loadPrivateKey(keyStoreName, KEYSTORE_KEY_ALIAS, password);
		loadPublicKey(keyStoreName, KEYSTORE_CERT_ALIAS, password);
	}

	// Initial the keys 
	private static void loadPrivateKey(String keyStoreName, String keyAliasName, String password) throws Exception {
		// Load KeyStore
		KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		FileInputStream keyStoreFile = new FileInputStream(keyStoreName);
		keyStore.load(keyStoreFile, password.toCharArray());

		// Get Private Key
		assert keyStore.isKeyEntry(keyAliasName);
		KeyStore.PrivateKeyEntry keyEntry = (KeyStore.PrivateKeyEntry) keyStore
				.getEntry(keyAliasName, new KeyStore.PasswordProtection(
						password.toCharArray()));
		privateKey = keyEntry.getPrivateKey();		
	}
	
	public static void loadPublicKey(String keyStoreName, String publicKeyAlias, String password) throws Exception {
		// Load KeyStore
		KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		FileInputStream keyStoreFile = new FileInputStream(keyStoreName);
		keyStore.load(keyStoreFile, password.toCharArray());

		assert keyStore.isCertificateEntry(publicKeyAlias);
		
		KeyStore.TrustedCertificateEntry certEntry = (KeyStore.TrustedCertificateEntry) keyStore
		.getEntry(publicKeyAlias, null);

		// Get Public Key
		publicKey = certEntry.getTrustedCertificate().getPublicKey();				
	}
	
	public Element encode(Document doc) {

		Element elm;

		Element root = doc.createElementNS(NS_URI, NS_PREFIX + ":signedMark");
		root.setAttribute("id", "signedMark");
		root.setIdAttribute("id", true);

		// <smd:serial> element
		XMLUtil.encodeString(doc, root, this.serial, NS_URI, NS_PREFIX
				+ ":serial");

		// <smd:zone> elements
		XMLUtil.encodeStringList(doc, root, this.zones, NS_URI, NS_PREFIX
				+ ":zone");

		// <smd:exDate> element
		XMLUtil.encodeTimeInstant(doc, root, this.exDate, NS_URI, NS_PREFIX
				+ ":exDate");

		// <smd:mark> element
		if (mark != null) {
			root.appendChild(mark.encode(doc));
		}
		
		return root;
	}

	public void signMark(Document doc, Element root) throws Exception {

		XMLSignatureFactory xmlSigFactory = XMLSignatureFactory.getInstance("DOM");
		
		DigestMethod digestMethod = xmlSigFactory.newDigestMethod(
				DigestMethod.SHA1, null);
		
		List transforms = new ArrayList<Transform>(); 
//		transforms.add(xmlSigFactory.newTransform(Transform.XPATH, new XPathFilterParameterSpec("ancestor-or-self::signedMark")));
		transforms.add(xmlSigFactory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null));
		
		
		Reference xmlSigRef = xmlSigFactory.newReference("#signedMark", digestMethod, transforms, null, null);
//		Reference xmlSigRef = xmlSigFactory.newReference("", digestMethod, transforms, null, null);
				
		// Create the SignedInfo
		SignedInfo signedInfo = xmlSigFactory
		.newSignedInfo(xmlSigFactory.newCanonicalizationMethod(
				CanonicalizationMethod.EXCLUSIVE,
				(C14NMethodParameterSpec) null), xmlSigFactory
				.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
				Collections.singletonList(xmlSigRef));

		// Create a DOMSignContext and specify the PrivateKey and
		// location of the resulting XMLSignature's parent element
		DOMSignContext signContext = new DOMSignContext(privateKey, root);
		signContext.setDefaultNamespacePrefix("dsig");

		// Create the XMLSignature (but don't sign it yet)
		XMLSignature signature = xmlSigFactory.newXMLSignature(signedInfo, null);
			
		// Marshal, generate (and sign) the enveloped signature
		signature.sign(signContext);
	}
	
	
	
	public String getSerial() {
		return this.serial;
	}

	public void setSerial(String aSerial) {
		this.serial = aSerial;
	}

	public List<String> getZones() {
		return this.zones;
	}

	public void setZones(List<String> aZones) {
		this.zones = aZones;
	}

	public Date getExDate() {
		return this.exDate;
	}

	public void setExDate(Date aExDate) {
		this.exDate = aExDate;
	}

	public Mark getMark() {
		return this.mark;
	}

	public void setMark(Mark aMark) {
		this.mark = aMark;
	}
	
	public byte[] toXML() throws Exception {
				
		// Create mark DOM
		Document markDoc = new DocumentImpl();
		Element markRoot = this.mark.encode(markDoc);
				
		// Output mark
		System.out.println("Mark = [");
		XMLUtil.marshalToStream(markRoot, System.out, true);
		System.out.println("]");
		
		// Create signed mark DOM
		Element signedMarkRoot = this.encode(markDoc);
		
		// Add the signed mark root element to the document 
		markDoc.appendChild(signedMarkRoot);
		// HAD TO DO THIS FOR THE UNMARSHAL VALIDATE TO WORK
		markDoc.normalizeDocument();

		// Output Signed Mark
		System.out.println("Signed Mark = [");
		XMLUtil.marshalToStream(signedMarkRoot, System.out, true);
		System.out.println("]");
		
		// Add signature to Signed Mark
		this.signMark(markDoc, signedMarkRoot);
		
		// Marshal XML to output stream buffer 
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		
		// Output Signed Mark with Signature
		System.out.println("Signed Mark with Signature from ByteArray = [");
		XMLUtil.marshalToStream(signedMarkRoot, os, false);
		System.out.print(os);
		System.out.println("]");
		
		// Output Signed Mark with Signature
		System.out.println("Signed Mark with Signature Indented = [");
		XMLUtil.marshalToStream(signedMarkRoot, System.out, true);
		System.out.println("]");
		
		
		// Base64 Encode the Signed Mark
		System.out.println("Base64 Encoded Mark = [");
		System.out.print(XMLUtil.encodeBase64(signedMarkRoot));
		System.out.println("]");
		
		System.out.println("Base64 Decoded XML Mark = [");
		System.out.print(new String(Base64.decodeBase64(XMLUtil.encodeBase64(signedMarkRoot))));
		System.out.println("]");
		
		
//		return os.toByteArray();	
		return Base64.decodeBase64(XMLUtil.encodeBase64(signedMarkRoot));
	}
	
	public void fromXML(byte[] xml) throws Exception {
		ByteArrayInputStream is = new ByteArrayInputStream(xml);
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		docFactory.setNamespaceAware(true);
//		docFactory.setIgnoringElementContentWhitespace(true);
//		docFactory.setValidating(true);
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(is);
		
		// TEST - Attempt to modify the <smd:serial> element from "123456" to "654321".
		// to test true XML Signature coverage.  
/*		NodeList serialNodeList = doc.getElementsByTagNameNS(NS_URI, "serial");
		if (serialNodeList.getLength() == 0) {
			throw new Exception("Cannot find xmd:serial element");
		}
		Element serialElm = (Element) serialNodeList.item(0);
		serialElm.setTextContent("654321");
*/		
		
		NodeList nodeList = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
		
		if (nodeList.getLength() == 0) {
			throw new Exception("Cannot find Signature element");
		}
		
		XMLSignatureFactory sigFactory = XMLSignatureFactory.getInstance("DOM");
		DOMValidateContext valContext = new DOMValidateContext(publicKey, nodeList.item(0));
		XMLSignature signature = sigFactory.unmarshalXMLSignature(valContext);
		if (signature.validate(valContext)) {
			System.out.println("Signature validation successful");
		}
		else {
			System.out.println("Signature validation unsuccessful");
		
			System.out.println("Signature validation status = " + signature.getSignatureValue().validate(valContext));
			
			// Check validation status of each Reference
			Iterator<?> i = signature.getSignedInfo().getReferences().iterator();
			
			for (int j = 0; i.hasNext(); j++) {
				System.out.println("ref["+j+"] validity status = " + ((Reference) i.next()).validate(valContext));
			}
			
		}
		
	}	
}
