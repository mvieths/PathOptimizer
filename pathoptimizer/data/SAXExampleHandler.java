/**
 * 
 */
package pathoptimizer.data;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Foeclan
 * 
 */

/*
 * SAX handler for Reactome 'participating molecule' lists
 */
// Format:
// <molecule>
// <Type>Proteins</Type>
// <Name>ERK1 [cytosol]</Name>
// <Uniprot_ID>P27361</Uniprot_ID>
// <Gene_Name>MAPK3</Gene_Name>
// <ChEBI_ID>N/A</ChEBI_ID>
// </molecule>

public class SAXExampleHandler extends DefaultHandler {
	private String type;
	private String name;
	private String uniprotID;
	private String geneName;
	private String chebiID;

	boolean isType = false;
	boolean isName = false;
	boolean isUniprotID = false;
	boolean isGeneName = false;
	boolean isChebiID = false;

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		if (qName.equalsIgnoreCase("Type")) {
			isType = true;
		}

		if (qName.equalsIgnoreCase("Name")) {
			isName = true;
		}

		if (qName.equalsIgnoreCase("Uniprot_ID")) {
			isUniprotID = true;
		}

		if (qName.equalsIgnoreCase("Gene_Name")) {
			isGeneName = true;
		}

		if (qName.equalsIgnoreCase("ChEBI_ID")) {
			isChebiID = true;
		}

	}

	public void endElement(String uri, String localName, String qName) throws SAXException {

		// System.out.println("End Element :" + qName);

	}

	public void characters(char ch[], int start, int length) throws SAXException {

		// System.out.println(new String(ch, start, length));

		if (isType) {
			type = new String(ch, start, length);
			isType = false;
		}

		if (isName) {
			name = new String(ch, start, length);
			isName = false;
		}

		if (isUniprotID) {
			uniprotID = new String(ch, start, length);
			isUniprotID = false;
		}

		if (isGeneName) {
			geneName = new String(ch, start, length);
			isGeneName = false;
		}

		if (isChebiID) {
			chebiID = new String(ch, start, length);
			isChebiID = false;
		}

	}

	public String getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public String getUniprotID() {
		return uniprotID;
	}

	public String getGeneName() {
		return geneName;
	}

	public String getChebiID() {
		return chebiID;
	}
}
