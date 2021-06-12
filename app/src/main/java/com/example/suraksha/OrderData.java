package com.example.suraksha;

public class OrderData
{
    int oId, oAmount;
    String oItems = "", oDate = "", oAddress = "";

    public int getoId() {
        return oId;
    }

    public void setoId(int oId) {
        this.oId = oId;
    }

    public int getoAmount() {
        return oAmount;
    }

    public void setoAmount(int oAmount) {
        this.oAmount = oAmount;
    }

    public String getoItems() {
        return oItems;
    }

    public void setoItems(String oItems) {
        this.oItems = oItems;
    }

    public String getoDate() {
        return oDate;
    }

    public void setoDate(String oDate) {
        this.oDate = oDate;
    }

    public String getoAddress() {
        return oAddress;
    }

    public void setoAddress(String oAddress) {
        this.oAddress = oAddress;
    }
}
