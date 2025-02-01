package wenchao.kiosk;

import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.MetadataOutput;
import com.google.android.exoplayer2.metadata.MetadataDecoderFactory;
import com.google.android.exoplayer2.metadata.MetadataDecoder;
import com.google.android.exoplayer2.metadata.MetadataInputBuffer;
import com.google.android.exoplayer2.util.ParsableByteArray;

public class CustomMetadataExtractor implements MetadataDecoderFactory {

    @Override
    public boolean supportsFormat(com.google.android.exoplayer2.Format format) {
        // Return true if this decoder can handle the format; check format.id or other properties
        return true; // Simplified for example purposes
    }

    @Override
    public MetadataDecoder createDecoder(com.google.android.exoplayer2.Format format) {
        return new CustomMetadataDecoder();
    }

    private static class CustomMetadataDecoder implements MetadataDecoder {
        @Override
        public Metadata decode(MetadataInputBuffer inputBuffer) {
            byte[] data = inputBuffer.data.array();
            if (data == null) {
                return null;
            }
            ParsableByteArray byteArray = new ParsableByteArray(data);
            // Parse the byteArray to extract metadata
            return parseEPGData(byteArray);
        }

        private Metadata parseEPGData(ParsableByteArray byteArray) {
            // Implement parsing logic here based on your EPG data format
            // Example: extract text, timestamps, etc.

            // Construct and return Metadata
            return new Metadata(new Metadata.Entry[]{ /* your parsed entries */ });
        }
    }
}
