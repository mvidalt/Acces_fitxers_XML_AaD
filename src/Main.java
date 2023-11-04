import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.*;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) throws ParserConfigurationException, SAXException,IOException
    {
        DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
        DocumentBuilder db=dbf.newDocumentBuilder();
        Document doc=db.parse(new File("bookings.xml"));

        NodeList nodes=doc.getElementsByTagName("booking");

        for(int i=0;i<nodes.getLength();i++){
            Node node=nodes.item(i);
            Element element=(Element)node;

            System.out.println("Location number:" + element.getAttribute("location_number"));
        }
    }

}