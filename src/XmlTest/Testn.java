package XmlTest;

import framework.xml.XmlDocument;
import framework.xml.XmlNode;

public class Testn {
	public static void main(String[] args){
		String string= "<News C_S=\"40\"><NewsItem Id=\"8b8499ee-24d6-46b0-9f5c-5fd7026e5424\" PublishTime=\"2013-06-04 11:53:37\" Language=\"Chs\" Title=\"11:53 港股 西王特钢 月报表\" /><NewsItem Id=\"837d4948-0bbe-4a84-a5c6-801c13feb59c\" PublishTime=\"2013-06-04 11:53:30\" Language=\"Chs\" Title=\"11:53 港股 恒指花旗四一牛A 债券及结构性产品,牛熊证到期公告\" /></News>";
		XmlDocument document = new XmlDocument();
		document.load(string);
		XmlNode node= document.get_DocumentElement();
		System.out.println(node.get_OuterXml());
	}
}
