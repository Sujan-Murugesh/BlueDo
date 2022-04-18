package com.sujan.bluedo.Model;

public class Categories {
    private String cid,name,description,image;

    public Categories() {
    }

    public Categories(String cid, String name, String description, String image) {
        this.cid = cid;
        this.name = name;
        this.description = description;
        this.image = image;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
