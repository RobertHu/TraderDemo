package Util;

import java.io.IOException;

import org.apache.log4j.Logger;

import framework.data.DataSet;
import framework.xml.XmlDocument;
import framework.xml.XmlNode;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;
import nu.xom.Text;
import nu.xom.ValidityException;
import org.apache.commons.lang3.StringEscapeUtils;

public class XmlElementHelper {
	private static Logger logger= Logger.getLogger(XmlElementHelper.class);
	public static Element parse(String node) throws ValidityException, ParsingException, IOException{
		//System.out.println(node);
		Document doc=new Builder().build(node, "");
		Element ele=doc.getRootElement();
		return ele;
	}
	
	public static Element create(String name,String value){
		Element element=new Element(name);
		element.appendChild(new Text(value));
		return element;
	}
	public static XmlNode ConvertToXmlNode(Element element)
	{
		XmlDocument doc = new XmlDocument();
		
		String xml = StringEscapeUtils.unescapeXml(element.getValue());
		//logger.debug(xml);
		doc.loadXml(xml);
		//logger.debug("load complete");
		return doc.get_DocumentElement();
	}

	public static DataSet convertToDataset(Element element)
	{
		DataSet ds = new DataSet();
		ds.readXml(ConvertToXmlNode(element));
		//logger.debug("okkkk");
		return ds;
	}
}
