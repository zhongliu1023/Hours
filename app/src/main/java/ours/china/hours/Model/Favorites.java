package ours.china.hours.Model;

import android.widget.ImageView;

public class Favorites {

    String imageUrl1;
    String imageUrl2;
    String imageUrl3;
    String imageUrl4;
    String favoriteType;
    String stateClick;

    public Favorites(String imageUrl1, String imageUrl2, String imageUrl3, String imageUrl4, String favoriteType, String stateClick) {
        this.imageUrl1 = imageUrl1;
        this.imageUrl2 = imageUrl2;
        this.imageUrl3 = imageUrl3;
        this.imageUrl4 = imageUrl4;
        this.favoriteType = favoriteType;
        this.stateClick = stateClick;
    }

    public Favorites(String imageUrl1, String imageUrl2, String imageUrl3, String favoriteType, String stateClick) {
        this.imageUrl1 = imageUrl1;
        this.imageUrl2 = imageUrl2;
        this.imageUrl3 = imageUrl3;
        this.favoriteType = favoriteType;
        this.stateClick = stateClick;
    }

    public Favorites(String imageUrl1, String imageUrl2, String favoriteType, String stateClick) {
        this.imageUrl1 = imageUrl1;
        this.imageUrl2 = imageUrl2;
        this.favoriteType = favoriteType;
        this.stateClick = stateClick;
    }

    public Favorites(String imageUrl1, String favoriteType, String stateClick) {
        this.imageUrl1 = imageUrl1;
        this.favoriteType = favoriteType;
        this.stateClick = stateClick;
    }

    public Favorites(String favoriteType, String stateClick) {
        this.favoriteType = favoriteType;
        this.stateClick = stateClick;
    }

    public String getImageUrl1() {
        return imageUrl1;
    }

    public void setImageUrl1(String imageUrl1) {
        this.imageUrl1 = imageUrl1;
    }

    public String getImageUrl2() {
        return imageUrl2;
    }

    public void setImageUrl2(String imageUrl2) {
        this.imageUrl2 = imageUrl2;
    }

    public String getImageUrl3() {
        return imageUrl3;
    }

    public void setImageUrl3(String imageUrl3) {
        this.imageUrl3 = imageUrl3;
    }

    public String getImageUrl4() {
        return imageUrl4;
    }

    public void setImageUrl4(String imageUrl4) {
        this.imageUrl4 = imageUrl4;
    }

    public String getFavoriteType() {
        return favoriteType;
    }

    public void setFavoriteType(String favoriteType) {
        this.favoriteType = favoriteType;
    }

    public String getStateClick() {
        return stateClick;
    }

    public void setStateClick(String stateClick) {
        this.stateClick = stateClick;
    }
}
