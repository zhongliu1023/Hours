package ours.china.hours.Model;

public class ReadingStatusBook {

    String imageUrl;
    String bookName;
    String scheduleNumber;
    String duration;
    String averageHours;

    public ReadingStatusBook(String imageUrl, String bookName, String scheduleNumber, String duration, String averageHours) {
        this.imageUrl = imageUrl;
        this.bookName = bookName;
        this.scheduleNumber = scheduleNumber;
        this.duration = duration;
        this.averageHours = averageHours;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getScheduleNumber() {
        return scheduleNumber;
    }

    public void setScheduleNumber(String scheduleNumber) {
        this.scheduleNumber = scheduleNumber;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getAverageHours() {
        return averageHours;
    }

    public void setAverageHours(String averageHours) {
        this.averageHours = averageHours;
    }
}
