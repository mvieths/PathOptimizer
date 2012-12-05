/**
 * 
 */
package pathoptimizer.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;

/**
 * @author Foeclan
 * 
 */
public class SAXExample {

	// Format:
	// <molecule>
	// <Type>Proteins</Type>
	// <Name>ERK1 [cytosol]</Name>
	// <Uniprot_ID>P27361</Uniprot_ID>
	// <Gene_Name>MAPK3</Gene_Name>
	// <ChEBI_ID>N/A</ChEBI_ID>
	// </molecule>

	private String molFile;
	private SAXExampleHandler molHandler = null;

	public SAXExample(String filename) {
		molFile = filename;
		loadMolecules();
	}

	/*
	 * This method will populate the object based on the contents of a given file
	 */
	public void loadMolecules() {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			File file = new File(molFile);
			InputStream inputStream = new FileInputStream(file);
			Reader reader = new InputStreamReader(inputStream, "UTF-8");

			InputSource is = new InputSource(reader);
			is.setEncoding("UTF-8");
			molHandler = new SAXExampleHandler();

			saxParser.parse(is, molHandler);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getType() {
		if (molHandler != null) {
			return molHandler.getType();
		} else {
			return "XML Parsing Error";
		}
	}

	public String getName() {
		if (molHandler != null) {
			return molHandler.getName();
		} else {
			return "XML Parsing Error";
		}
	}

	public String getUniprotID() {
		if (molHandler != null) {
			return molHandler.getUniprotID();
		} else {
			return "XML Parsing Error";
		}
	}

	public String getGeneName() {
		if (molHandler != null) {
			return molHandler.getGeneName();
		} else {
			return "XML Parsing Error";
		}
	}

	public String getChebiID() {
		if (molHandler != null) {
			return molHandler.getChebiID();
		} else {
			return "XML Parsing Error";
		}
	}
}
