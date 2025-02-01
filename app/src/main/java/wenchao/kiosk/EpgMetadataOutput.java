package wenchao.kiosk;

import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.MetadataOutput;
import com.google.android.exoplayer2.metadata.id3.TextInformationFrame;

class EpgMetadataOutput implements MetadataOutput {
    private final MetadataOutput output;
    private String epgData;

    public EpgMetadataOutput(MetadataOutput output) {
        this.output = output;
    }

    @Override
    public void onMetadata(Metadata metadata) {
        epgData = null;
        for (int i = 0; i < metadata.length(); i++) {
            Metadata.Entry entry = metadata.get(i);
            if (entry instanceof TextInformationFrame) {
                TextInformationFrame textFrame = (TextInformationFrame) entry;
                if ("TPE1".equals(textFrame.id)) {
                    epgData = textFrame.value;
                    break;
                }
            }
        }
        output.onMetadata(metadata);
    }

    public String getEpgData() {
        return epgData;
    }
}