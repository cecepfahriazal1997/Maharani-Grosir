package com.application.mgoaplication.model;

public class ProfileModel {
    private String id, title, subtitle;
    private int image, colorImage, colorTitle, colorSubtitle;

    public ProfileModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getColorTitle() {
        return colorTitle;
    }

    public void setColorTitle(int colorTitle) {
        this.colorTitle = colorTitle;
    }

    public int getColorSubtitle() {
        return colorSubtitle;
    }

    public void setColorSubtitle(int colorSubtitle) {
        this.colorSubtitle = colorSubtitle;
    }

    public int getColorImage() {
        return colorImage;
    }

    public void setColorImage(int colorImage) {
        this.colorImage = colorImage;
    }
}
