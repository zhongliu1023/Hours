package ours.china.hours.Model;

public class NewsItem {
    String newsType;
    String newsTime;
    String newsContent;

    public NewsItem(String newsType, String newsTime, String newsContent) {
        this.newsType = newsType;
        this.newsTime = newsTime;
        this.newsContent = newsContent;
    }

    public NewsItem() {
    }

    public String getNewsType() {
        return newsType;
    }

    public void setNewsType(String newsType) {
        this.newsType = newsType;
    }

    public String getNewsTime() {
        return newsTime;
    }

    public void setNewsTime(String newsTime) {
        this.newsTime = newsTime;
    }

    public String getNewsContent() {
        return newsContent;
    }

    public void setNewsContent(String newsContent) {
        this.newsContent = newsContent;
    }
}
