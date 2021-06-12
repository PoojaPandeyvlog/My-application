package com.example.suraksha;

public class CartData
{
    int[] productIcons = {R.drawable.ic_pocket_knife, R.drawable.ic_pepper_spray,
            R.drawable.ic_knuckles, R.drawable.ic_taser, R.drawable.ic_lipstick};

    int pId;
    int pAmount;
    int pIcon;
    int pQty;
    int pPrice;

    public int getpPrice() {
        return pPrice;
    }

    public void setpPrice(int pPrice) {
        this.pPrice = pPrice;
    }

    String pName;

    public int getpQty() {
        return pQty;
    }

    public void setpQty(int pQty) {
        this.pQty = pQty;
    }

    public int getProductIcon() {
        return pIcon;
    }

    public void setProductIcon()
    {
        this.pIcon = productIcons[pId];
    }

    public int getpId() {
        return pId;
    }

    public void setpId(int pId) {
        this.pId = pId;
    }

    public int getpAmount() {
        return pAmount;
    }

    public void setpAmount(int pAmount) {
        this.pAmount = pAmount;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

}
