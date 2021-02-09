package bkz.app.eastbeats.Models;



public class MediaObject {

    private String title;
    private String media_url;
    private String description;
    private Double trend;

    public MediaObject(String title, String description,String media_url, Double trend) {
        this.title = title;
        this.media_url = media_url;
        this.description = description;
        this.trend = trend;

    }

    public MediaObject() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMedia_url() {
        return media_url;
    }

    public void setMedia_url(String media_url) {
        this.media_url = media_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double  getTrend() {
        return trend;
    }

    public void setTrend(Double trend) {
        this.trend = trend;
    }
}