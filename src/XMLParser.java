import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class XMLParser {


    public XMLParser() {

    }

    public Activity parse(File file) {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);

            document.getDocumentElement().normalize();

            Node gpxNode = document.getElementsByTagName("gpx").item(0);
            Element gpxElement = (Element) gpxNode;
            String userID = gpxElement.getAttribute("creator");

            NodeList waypoints = document.getElementsByTagName("wpt");
            ArrayList<Waypoint> actWpts = new ArrayList<>();

            for (int i = 0; i < waypoints.getLength(); i++) {
                Node waypoint = waypoints.item(i);

                if (waypoint.getNodeType() == Node.ELEMENT_NODE) {
                    Element waypointElement = (Element) waypoint;

                    double lat = Double.parseDouble(waypointElement.getAttribute("lat"));
                    double lon = Double.parseDouble(waypointElement.getAttribute("lon"));
                    float ele = Float.parseFloat(waypointElement.getElementsByTagName("ele").item(0).getTextContent());

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    LocalDateTime time = LocalDateTime.parse(waypointElement.getElementsByTagName("time").item(0).getTextContent(),formatter);

                    Waypoint wpt = new Waypoint(lat,lon,ele,time);
                    actWpts.add(wpt);
                }
            }

            Activity act = new Activity(userID,actWpts);
            return act;

        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }
}
