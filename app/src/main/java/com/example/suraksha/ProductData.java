package com.example.suraksha;

public class ProductData
{
    int[] productIcons = {R.drawable.ic_pocket_knife, R.drawable.ic_pepper_spray,
            R.drawable.ic_knuckles, R.drawable.ic_taser, R.drawable.ic_lipstick};

    int pId, pPrice, pIcon; String pName, pInfo; Boolean itemInCart = false;

    public Boolean getItemInCart() {
        return itemInCart;
    }

    public void setItemInCart(Boolean itemInCart) {
        this.itemInCart = itemInCart;
    }

    public int getProductIcon() {
        return pIcon;
    }

    public void setProductIcon(int i) {
        this.pIcon = productIcons[i];
    }

    public int getpId() {
        return pId;
    }

    public void setpId(int pId) {
        this.pId = pId;
    }

    public int getpPrice() {
        return pPrice;
    }

    public void setpPrice(int pPrice) {
        this.pPrice = pPrice;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public String getpInfo() {
        return pInfo;
    }

    public void setpInfo(String pInfo) {
        this.pInfo = pInfo;
    }
}
