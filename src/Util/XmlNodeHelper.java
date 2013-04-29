package Util;

import org.apache.commons.lang3.StringEscapeUtils;

import framework.xml.XmlDocument;
import framework.xml.XmlNode;

public final class XmlNodeHelper {
	public static XmlNode Parse(String xml){
		XmlDocument document=new XmlDocument();
		document.loadXml(StringEscapeUtils.unescapeXml(xml));
		return document.get_DocumentElement();
	}
}
