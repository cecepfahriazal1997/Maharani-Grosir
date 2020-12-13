package com.application.mgoaplication.model;

public class CategoryModel {
    private String id, title, total, image;
    private boolean haveCategory;

    public CategoryModel() {
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

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isHaveCategory() {
        return haveCategory;
    }

    public void setHaveCategory(boolean haveCategory) {
        this.haveCategory = haveCategory;
    }
}
