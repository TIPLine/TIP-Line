package io.github.tipline.android_app;

import android.util.Xml;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;


public class XMLGenerator {
    //Maybe make a TIP (super) Class it can take in?
    //Or make a class with the XML tags?
    //Fill in the appropriate information

    public String createXML(String type, String name, String locationCountry, double locationLongitude, double locationLatitude, String phoneNumber, String
                            title, String body, List<File> attachments) throws IOException {

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

        //<locationCountry>
        serializer.startTag("", "locationCountry");
        serializer.text(locationCountry);
        serializer.endTag("", "locationCountry");
        //</locationCountry>

        //<locationLongitude>
        serializer.startTag("", "locationLongitude");
        serializer.text(Double.toString(locationLongitude));
        serializer.endTag("", "locationLongitude");
        //</locationLongitude>

        //<locationLatitude>
        serializer.startTag("", "locationLatitude");
        serializer.text(Double.toString(locationLatitude));
        serializer.endTag("", "locationLatitude");
        //</locationLatitude>
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

        for (File attachment: attachments) {
            //<attachment> or File
            serializer.startTag("", "attachment");
            serializer.text(attachment.getName());
            serializer.endTag("", "attachment");
            //</attachment>
        }

        serializer.endTag("", "tip");
        //</tip>

        //End Document
        serializer.endDocument();
        return writer.toString();
    }
    public String createXML(String type, String name, String locationCountry, double locationLongitude, double locationLatitude, String phoneNumber, String
            title, String body, File file) throws IOException {
        List<File> attachments = new ArrayList<>();
        attachments.add(file);
        return createXML(type, name, locationCountry, locationLongitude, locationLatitude, phoneNumber, title, body, attachments);
    }

    public String createXML(String type, String name, String locationCountry, double locationLongitude, double locationLatitude, String phoneNumber, String
            title, String body) throws IOException {
        List<File> attachments = new ArrayList<>(); //empty attachments list

        return createXML(type, name, locationCountry, locationLongitude, locationLatitude, phoneNumber, title, body, attachments);
    }
}
