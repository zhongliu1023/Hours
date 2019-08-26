package ours.china.hours.Model;

public class Profile {

    String startText;
    String endText;

    public Profile(String startText, String endText) {
        this.startText = startText;
        this.endText = endText;
    }

    public String getStartText() {
        return startText;
    }

    public void setStartText(String startText) {
        this.startText = startText;
    }

    public String getEndText() {
        return endText;
    }

    public void setEndText(String endText) {
        this.endText = endText;
    }
}
