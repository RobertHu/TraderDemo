package XmlTest;

import framework.xml.XmlDocument;
import framework.xml.XmlNode;

public class Testn {
	public static void main(String[] args){
		String string= "<News C_S=\"40\"><NewsItem Id=\"8b8499ee-24d6-46b0-9f5c-5fd7026e5424\" PublishTime=\"2013-06-04 11:53:37\" Language=\"Chs\" Title=\"11:53 �۹� �����ظ� �±���\" /><NewsItem Id=\"837d4948-0bbe-4a84-a5c6-801c13feb59c\" PublishTime=\"2013-06-04 11:53:30\" Language=\"Chs\" Title=\"11:53 �۹� ��ָ������һţA ծȯ���ṹ�Բ�Ʒ,ţ��֤���ڹ���\" /></News>";
		XmlDocument document = new XmlDocument();
		document.load(string);
		XmlNode node= document.get_DocumentElement();
		System.out.println(node.get_OuterXml());
	}
}
