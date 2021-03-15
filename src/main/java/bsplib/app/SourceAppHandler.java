package bsplib.app;

import java.io.*;
import java.util.*;
import java.util.logging.*;
import log.*;
import org.apache.commons.io.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

/**
 * Application database SAX handler.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class SourceAppHandler extends DefaultHandler {

    private static final Logger L = LogUtils.getLogger();

    private List<SourceApp> appList = new ArrayList<>();
    private SourceApp currentApp;
    private String currentElement;
    private StringBuilder entitiesBuf = new StringBuilder(4096);

    @Override
    public void startDocument() throws SAXException {
        appList.clear();
        currentApp = null;
        currentElement = null;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        currentElement = qName;

        if (qName.equals("app")) {
            String name = attributes.getValue("name");
            String id = attributes.getValue("id");

            if (name == null) {
                L.warning("Ignoring app with missing attribute \"name\"");
            } else if (id == null) {
                L.warning("Ignoring app with missing attribute \"id\"");
            } else {
                currentApp = new SourceApp(name, Integer.valueOf(id));
            }
        }

        if (currentApp == null) {
            return;
        }

        switch (qName) {
            case "version":
                String minVers = attributes.getValue("min");
                String maxVers = attributes.getValue("max");
                if (minVers != null) {
                    currentApp.setVersionMin(Integer.valueOf(minVers));
                }
                if (maxVers != null) {
                    currentApp.setVersionMax(Integer.valueOf(maxVers));
                }
                break;

            case "entities":
                String pointsEntities = attributes.getValue("points");
                if (pointsEntities != null) {
                    currentApp.setPointsEntities(Float.valueOf(pointsEntities));
                }
                break;

            case "files":
                String pointsFiles = attributes.getValue("points");
                if (pointsFiles != null) {
                    currentApp.setPointsFilePattern(Float.valueOf(pointsFiles));
                }
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("app")) {
            appList.add(currentApp);
            currentApp = null;
        }

        if (currentApp == null) {
            return;
        }

        if (qName.equals("entities")) {
            LineIterator it = new LineIterator(new StringReader(entitiesBuf.toString()));

            while (it.hasNext()) {
                String entity = it.next().trim();
                if (!entity.isEmpty()) {
                    currentApp.getEntities().add(entity);
                }
            }

            // clear buffer without re-allocating it
            entitiesBuf.setLength(0);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (currentApp == null) {
            return;
        }
        switch (currentElement) {
            case "entities":
                entitiesBuf.append(ch, start, length);
                break;

            case "files":
                String content = new String(ch, start, length);
                if (!content.trim().isEmpty()) {
                    currentApp.setFilePattern(content);
                }
                break;
        }
    }

    public List<SourceApp> getAppList() {
        return Collections.unmodifiableList(appList);
    }
}
