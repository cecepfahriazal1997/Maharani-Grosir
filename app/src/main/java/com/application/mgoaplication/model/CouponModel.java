package com.application.mgoaplication.model;

public class CouponModel {
    private String id, title, description, termCondition, amount;

    public CouponModel() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTermCondition() {
        return termCondition;
    }

    public void setTermCondition(String termCondition) {
        this.termCondition = termCondition;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
