package wenchao.kiosk;

public class Channel {
    private String id;
    private String name;
    private String logoUrl;
    private String channelUrl;
    private String category;
    private int logoResId;
    public Channel(String id, String name, String logoUrl, String channelUrl, String category) {
        this.id = id;
        this.name = name;
        this.logoUrl = logoUrl;
        this.channelUrl = channelUrl;
        this.category = category;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getChannelUrl() {
        return channelUrl;
    }
    public int getLogoResId() {
        return logoResId;
    }
    public String getCategory() {
        return category;
    }

    // Setters, if necessary
}
