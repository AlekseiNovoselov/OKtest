package com.lexaloris.recyclevideoview;

public class Video {

    String id;
    String indexPosition;
    String url;

    public Video(String id, String indexPosition, String url) {
        this.id = id;
        this.indexPosition = indexPosition;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getId() {
        return id;
    }

    public String getIndexPosition() {
        return indexPosition;
    }
}
