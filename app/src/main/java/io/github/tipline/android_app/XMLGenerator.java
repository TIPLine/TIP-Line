package io.github.tipline.android_app;

import android.util.Xml;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;


public class XMLGenerator {
    //Maybe make a TIP (super) Class it can take in?
    //Or make a class with the XML tags?
    //Fill in the appropriate information

    public String createXML(String type, String name, String location, String phoneNumber, String
                            title, String body, List<String> filePaths) throws IOException {

        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        serializer.setOutput(writer);
        //Start document
        serializer.startDocument("UTF-8", true);

        //Open Tag <tip>
        serializer.startTag("", "tip");
        serializer.attribute("", "type", type);

        //<name>
        serializer.startTag("", "name");
        serializer.text(name);
        serializer.endTag("", "name");
        //</name>

        //<location>
        serializer.startTag("", "location");
        serializer.text(location);
        serializer.endTag("", "location");
        //</location>

        //<phone_number>
        serializer.startTag("", "phone_number");
        serializer.text(phoneNumber);
        serializer.endTag("", "phone_number");
        //</phone_number>

        //<title> or Subject
        serializer.startTag("", "title");
        serializer.text(title);
        serializer.endTag("", "title");
        //</title>

        //<body> or Message
        serializer.startTag("", "body");
        serializer.text(body);
        serializer.endTag("", "body");
        //</body>

        for (String filePath: filePaths) {
            //<attachment> or File
            serializer.startTag("", "attachment");
            serializer.text(filePath);
            serializer.endTag("", "attachment");
            //</attachment>
        }

        serializer.endTag("", "tip");
        //</tip>

        //End Document
        serializer.endDocument();

        return writer.toString();
    }
    public String createXML(String type, String name, String location, String phoneNumber, String
            title, String body, String filePath) throws IOException {
        List<String> filePaths = new ArrayList<>();
        filePaths.add(filePath);
        return createXML(type, name, location, phoneNumber, title, body, filePaths);
    }
}
