package ours.china.hours.Model;

public class SelectFavorite {

    String favorite;
    boolean checkState = false;

    public SelectFavorite(String favorite, boolean checkState) {
        this.favorite = favorite;
        this.checkState = checkState;
    }

    public String getFavorite() {
        return favorite;
    }

    public void setFavorite(String favorite) {
        this.favorite = favorite;
    }

    public boolean isCheckState() {
        return checkState;
    }

    public void setCheckState(boolean checkState) {
        this.checkState = checkState;
    }
}
