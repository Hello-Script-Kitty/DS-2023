package org.opa.ds23.gpxr.common.data;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class ActivityParser {

  public static Activity parse(String xml) throws ParserConfigurationException, IOException, SAXException {
    Document document = loadXMLFromString(xml);
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
        NodeList time1 = waypointElement.getElementsByTagName("time");
        Node item = time1.item(0);
        String textContent = item.getTextContent();
        LocalDateTime time = LocalDateTime.parse(textContent, formatter);

        Waypoint wpt = new Waypoint(lat, lon, ele, time);
        actWpts.add(wpt);
      }
    }

    return new Activity(userID, actWpts);
  }

  public static Document loadXMLFromString(String xml) throws ParserConfigurationException, IOException, SAXException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    InputSource is = new InputSource(new StringReader(xml));
    return builder.parse(is);
  }
}
