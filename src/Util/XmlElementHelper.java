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
		Document doc=new Builder().build(node, "");
		Element ele=doc.getRootElement();
		return ele;
	}
	
	public static Element create(String name,String value){
		Element element=new Element(name);
		element.appendChild(new Text(value));
		return element;
	}
	public static XmlNode ConvertToXmlNode(String element)
	{
		XmlDocument doc = new XmlDocument();
		//logger.debug(element);
		doc.loadXml(element);
		//logger.debug("load complete");
		return doc.get_DocumentElement();
	}
	
	
	public static DataSet convertToDataSet(String content){
		DataSet ds = new DataSet();
		ds.readXml(ConvertToXmlNode(content));
		return ds;
	}

	public static DataSet convertToDataset(Element element)
	{
		DataSet ds = new DataSet();
		ds.readXml(ConvertToXmlNode(element.getValue()));
		//logger.debug("okkkk");
		return ds;
	}
}
