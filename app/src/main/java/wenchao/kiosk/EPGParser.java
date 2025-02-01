package wenchao.kiosk;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EPGParser {
    private static final String TAG = "EPGParser";

    public List<EPGEvent> parseEPG(byte[] epgData) {
        List<EPGEvent> epgEvents = new ArrayList<>();
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new ByteArrayInputStream(epgData), "UTF-8");

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && "event".equals(parser.getName())) {
                    EPGEvent epgEvent = parseEPGEvent(parser);
                    epgEvents.add(epgEvent);
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            Log.e(TAG, "Error parsing EPG data", e);
        }
        return epgEvents;
    }

    private EPGEvent parseEPGEvent(XmlPullParser parser) throws IOException, XmlPullParserException {
        EPGEvent epgEvent = new EPGEvent();

        String id = parser.getAttributeValue(null, "id");
        String title = parser.getAttributeValue(null, "title");
        String description = parser.getAttributeValue(null, "description");
        long startTime = Long.parseLong(parser.getAttributeValue(null, "start_time"));
        long endTime = Long.parseLong(parser.getAttributeValue(null, "end_time"));

        epgEvent.setId(id);
        epgEvent.setTitle(title);
        epgEvent.setDescription(description);
        epgEvent.setStartTime(startTime);
        epgEvent.setEndTime(endTime);

        // Ensure we skip to the end tag of the event
        while (parser.next() != XmlPullParser.END_TAG) {
            // Skip to the end of the tag
        }

        return epgEvent;
    }
}

class EPGEvent {
    private String id;
    private String title;
    private String description;
    private long startTime;
    private long endTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    // Getters and setters omitted for brevity
}
