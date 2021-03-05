package com.amansiol.goonline.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
    This is Product Custom class which is used to
    collect all the necessary details regarding
    one product
 */
public class Product implements Serializable {

    String product_short_name;
    String shop_name;
    String product_price;
    String product_long_title;
    String product_color;
    List<String> keywords = new ArrayList<String>();
    String discount;
    String product_type;
    String gender;
    String product_desc;
    String image1;
    String image2;
    String image3;
    String image4;
    String id;
    String fullkeyword;
    String shopId;

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getFullkeyword() {
        return fullkeyword;
    }

    public void setFullkeyword(String fullkeyword) {
        this.fullkeyword = fullkeyword;
    }

    // empty constructor
    public Product() {

    }
    // constructor

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProduct_short_name() {
        return product_short_name;
    }

    public void setProduct_short_name(String product_short_name) {
        this.product_short_name = product_short_name;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    public String getProduct_long_title() {
        return product_long_title;
    }

    public void setProduct_long_title(String product_long_title) {
        this.product_long_title = product_long_title;
    }

    public String getProduct_color() {
        return product_color;
    }

    public void setProduct_color(String product_color) {
        this.product_color = product_color;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getProduct_type() {
        return product_type;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProduct_desc() {
        return product_desc;
    }

    public void setProduct_desc(String product_desc) {
        this.product_desc = product_desc;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getImage3() {
        return image3;
    }

    public void setImage3(String image3) {
        this.image3 = image3;
    }

    public String getImage4() {
        return image4;
    }

    public void setImage4(String image4) {
        this.image4 = image4;
    }
}
