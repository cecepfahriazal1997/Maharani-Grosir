package com.application.mgoaplication.model;

import java.util.List;

public class TransactionModel {
    private String id, orderNumber, date, total, amount, status;
    private List<ProductModel> item;

    public TransactionModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ProductModel> getItem() {
        return item;
    }

    public void setItem(List<ProductModel> item) {
        this.item = item;
    }
}
