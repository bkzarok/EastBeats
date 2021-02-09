package bkz.app.eastbeats.Models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Post {

    private String author;
    private String caption;
    private String videoUrl;

    private Double trend;


    public Post(String author, String caption, String videoUrl,Double trend)
    {
        this.author = author;
        this.videoUrl = videoUrl;
        this.caption = caption;
        this.trend = trend;
    }

    public void setVideoUrl(String url)
    {
        this.videoUrl = videoUrl;
    }

    public String getAuthor(){return author;}

    public String getCaption(){return caption;}

    public String getVideoUrl(){return videoUrl;}

    public Double getTrend(){return  trend;}
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("author", author);
        result.put("caption", caption);
        result.put("videoUrl", videoUrl);
        result.put("trend", trend);

        return result;
    }
}
