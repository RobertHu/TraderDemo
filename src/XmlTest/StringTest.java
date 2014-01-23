package XmlTest;

import framework.DateTime;
import framework.xml.XmlDocument;
import framework.xml.XmlElement;
import framework.xml.XmlNode;
import Util.StringHelper;

public class StringTest {
	public static void main(String[] args){
		String  inputString = "1126/4:137.69:137.67:137.85:137.65::68122248::;2:1565:1565:1575:1545::68122248::;16:1.14390:1.14390:1.15800:1.12300::68122248::/";
		XmlNode result = buildQuotationCommand(inputString);
		System.out.println(result.get_OuterXml());
	}
	
	private static XmlNode buildQuotationCommand(String content){
		String  startAndEndSeparator = "/";
		String quotationSeparator = ";";
		String fieldSeparator = ":";
		char rowSeparator ='\n';
		char colSeparator = '\t';
		String rowSeparatorString = new String(new char[]{rowSeparator});
		String colSeparatorString = new String(new char[]{colSeparator});
		int startIndex = content.indexOf(startAndEndSeparator);
		int endIndex = content.lastIndexOf(startAndEndSeparator);
		String commandSequence=content.substring(0, startIndex);
		String quotationContent = content.substring(startIndex + 1 , endIndex);
		String[] quotations =quotationContent.split(quotationSeparator); 
		XmlDocument xmlDocument = new XmlDocument();
		XmlElement commandsElement = xmlDocument.createElement("Commands");
		
		
		DateTime base = new DateTime(2011, 4, 1, 0, 0, 0);
		StringBuilder sBuilder = new StringBuilder();
		for (String quotation : quotations) {
			if(StringHelper.IsNullOrEmpty(quotation)){
				continue;
			}
			String[] quotationCols = quotation.split(fieldSeparator);
			String volumn ="";
			if(quotationCols.length==9){
				volumn= StringHelper.IsNullOrEmpty(quotationCols[7])?"":colSeparator+quotationCols[7]+colSeparator+quotationCols[8];
			}
			DateTime currentDateTime=base.addSeconds(Double.parseDouble(quotationCols[6]));
			String quotationString = quotationCols[0]+colSeparator+
					currentDateTime.toString("yyyy-MM-dd HH:mm:ss")+colSeparator+quotationCols[1]+colSeparator+quotationCols[2]+colSeparator+quotationCols[3]+colSeparator+quotationCols[4]+volumn;
			sBuilder.append(quotationString+rowSeparatorString);
		}
		if(sBuilder.length()>0){
			sBuilder.setLength(sBuilder.length() - rowSeparatorString.length());
		}
		XmlElement quotationElement2 = new XmlDocument().createElement("Quotation");
		quotationElement2.setAttribute("Overrided", sBuilder.toString());
		commandsElement.appendChild(quotationElement2);
		commandsElement.setAttribute("FirstSequence", commandSequence);
		commandsElement.setAttribute("LastSequence", commandSequence);
		return commandsElement;
	}
}
