package wenchao.kiosk;

import android.util.Log;

import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.MetadataOutput;
import com.google.android.exoplayer2.metadata.id3.Id3Frame;
import com.google.android.exoplayer2.metadata.id3.TextInformationFrame;


public class MetadataListener implements MetadataOutput {
    @Override
    public void onMetadata(Metadata metadata) {
        for (int i = 0; i < metadata.length(); i++) {
            Metadata.Entry entry = metadata.get(i);
            if (entry instanceof TextInformationFrame) {
                TextInformationFrame frame = (TextInformationFrame) entry;
                if ("TIT2".equals(frame.id)) {  // Check for the frame ID relevant to your data
                    String episodeName = frame.value;
                    Log.d("HLS Metadata", "Episode Name: " + episodeName);
                    // Update UI or handle the extracted metadata as needed
                }
            }
        }
    }
}
