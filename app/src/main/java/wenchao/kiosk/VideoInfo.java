package wenchao.kiosk;

import com.google.gson.annotations.SerializedName;

public class VideoInfo {
    @SerializedName("id")
    private String id;

    @SerializedName("video_url")
    private String videoUrl;

    @SerializedName("timestamp")
    private String timestamp;

    // Getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
